package com.liuyun.log4j.helpers;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Properties;
import com.liuyun.log4j.Hierarchy;
import com.liuyun.log4j.Level;
import com.liuyun.log4j.PropertyConfigurator;
import com.liuyun.log4j.xml.DomConfigurator;
import com.liuyun.log4j.spi.Configurator;
import com.liuyun.log4j.spi.LoggerRepository;

//属性值转换器
public class OptionConverter {

    static String DELIM_START = "${";
    static char DELIM_STOP = '}';
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN = 1;

    private OptionConverter() {}

    public static String[] concatanateArrays(String[] l, String[] r) {
        int len = l.length + r.length;
        String[] a = new String[len];

        System.arraycopy(l, 0, a, 0, l.length);
        System.arraycopy(r, 0, a, l.length, r.length);

        return a;
    }

    public static String convertSpecialChars(String s) {
        char c;
        int len = s.length();
        StringBuffer sbuf = new StringBuffer(len);

        int i = 0;
        while (i < len) {
            c = s.charAt(i++);
            if (c == '\\') {
                c = s.charAt(i++);
                if (c == 'n') {
                    c = '\n';
                } else if (c == 'r') {
                    c = '\r';
                } else if (c == 't') {
                    c = '\t';
                } else if (c == 'f') {
                    c = '\f';
                } else if (c == '\b') {
                    c = '\b';
                } else if (c == '\"') {
                    c = '\"';
                } else if (c == '\'') {
                    c = '\'';
                } else if (c == '\\') {
                    c = '\\';
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }


    //获取系统属性
    public static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (Throwable e) { // MS-Java throws com.ms.security.SecurityExceptionEx
            LogLog.debug("Was not allowed to read system property \"" + key + "\".");
            return def;
        }
    }


    public static Object instantiateByKey(Properties props, String key, Class superClass,
                                          Object defaultValue) {

        // Get the value of the property in string form
        String className = findAndSubst(key, props);
        if (className == null) {
            LogLog.error("Could not find value for key " + key);
            return defaultValue;
        }
        // Trim className to avoid trailing spaces that cause problems.
        return OptionConverter.instantiateByClassName(className.trim(), superClass, defaultValue);
    }

    //转换成boolean类型
    public static boolean toBoolean(String value, boolean dEfault) {
        if (value == null) {
            return dEfault;
        }
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            return false;
        }
        return dEfault;
    }

    //转换成int类型
    public static int toInt(String value, int dEfault) {
        if (value != null) {
            String s = value.trim();
            try {
                return Integer.valueOf(s).intValue();
            } catch (NumberFormatException e) {
                LogLog.error("[" + s + "] is not in proper int form.");
                e.printStackTrace();
            }
        }
        return dEfault;
    }

    //将属性转换成对应级别
    public static Level toLevel(String value, Level defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        value = value.trim();

        int hashIndex = value.indexOf('#');
        if (hashIndex == -1) {
            if ("NULL".equalsIgnoreCase(value)) {
                return null;
            } else {
                // no class name specified : use standard Level class
                return Level.toLevel(value, defaultValue);
            }
        }

        Level result = defaultValue;

        String clazz = value.substring(hashIndex + 1);
        String levelName = value.substring(0, hashIndex);

        // This is degenerate case but you never know.
        if ("NULL".equalsIgnoreCase(levelName)) {
            return null;
        }

        LogLog.debug("toLevel" + ":class=[" + clazz + "]"
                + ":pri=[" + levelName + "]");

        try {
            Class customLevel = Loader.loadClass(clazz);

            // get a ref to the specified class' static method
            // toLevel(String, Level)
            Class[] paramTypes = new Class[]{String.class,
                    Level.class
            };
            java.lang.reflect.Method toLevelMethod =
                    customLevel.getMethod("toLevel", paramTypes);

            // now call the toLevel method, passing level string + default
            Object[] params = new Object[]{levelName, defaultValue};
            Object o = toLevelMethod.invoke(null, params);

            result = (Level) o;
        } catch (ClassNotFoundException e) {
            LogLog.warn("custom level class [" + clazz + "] not found.");
        } catch (NoSuchMethodException e) {
            LogLog.warn("custom level class [" + clazz + "]"
                    + " does not have a class function toLevel(String, Level)", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getTargetException() instanceof InterruptedException
                    || e.getTargetException() instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.warn("custom level class [" + clazz + "]"
                    + " could not be instantiated", e);
        } catch (ClassCastException e) {
            LogLog.warn("class [" + clazz
                    + "] is not a subclass of Level", e);
        } catch (IllegalAccessException e) {
            LogLog.warn("class [" + clazz +
                    "] cannot be instantiated due to access restrictions", e);
        } catch (RuntimeException e) {
            LogLog.warn("class [" + clazz + "], level [" + levelName +
                    "] conversion failed.", e);
        }
        return result;
    }

    //将属性转化成对应文件大小
    public static long toFileSize(String value, long dEfault) {
        if (value == null) {
            return dEfault;
        }

        String s = value.trim().toUpperCase();
        long multiplier = 1;
        int index;

        if ((index = s.indexOf("KB")) != -1) {
            multiplier = 1024;
            s = s.substring(0, index);
        } else if ((index = s.indexOf("MB")) != -1) {
            multiplier = 1024 * 1024;
            s = s.substring(0, index);
        } else if ((index = s.indexOf("GB")) != -1) {
            multiplier = 1024 * 1024 * 1024;
            s = s.substring(0, index);
        }
        if (s != null) {
            try {
                return Long.valueOf(s).longValue() * multiplier;
            } catch (NumberFormatException e) {
                LogLog.error("[" + s + "] is not in proper int form.");
                LogLog.error("[" + value + "] not in expected format.", e);
            }
        }
        return dEfault;
    }

    /**
     * Find the value corresponding to <code>key</code> in
     * <code>props</code>. Then perform variable substitution on the
     * found value.
     */
    public static String findAndSubst(String key, Properties props) {
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }

        try {
            return substVars(value, props);
        } catch (IllegalArgumentException e) {
            LogLog.error("Bad option value [" + value + "].", e);
            return value;
        }
    }

    //通过类型名称返回配置器实例
    public static Object instantiateByClassName(String className, Class<?> superClass, Object defaultValue) {
        if (className != null) {
            try {
                //加载对应名称的类
                Class<?> classObj = Loader.loadClass(className);
                //若该类型不是指定类型的子类，则返回默认值
                if (!superClass.isAssignableFrom(classObj)) {
                    LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
                    LogLog.error("The class \"" + superClass.getName() + "\" was loaded by ");
                    LogLog.error("[" + superClass.getClassLoader() + "] whereas object of type ");
                    LogLog.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader() + "].");
                    return defaultValue;
                }
                //获取对应类型的实例
                return classObj.newInstance();
            } catch (ClassNotFoundException e) {
                LogLog.error("Could not instantiate class [" + className + "].", e);
            } catch (IllegalAccessException e) {
                LogLog.error("Could not instantiate class [" + className + "].", e);
            } catch (InstantiationException e) {
                LogLog.error("Could not instantiate class [" + className + "].", e);
            } catch (RuntimeException e) {
                LogLog.error("Could not instantiate class [" + className + "].", e);
            }
        }
        return defaultValue;
    }


    /**
     * Perform variable substitution in string <code>val</code> from the
     * values of keys found in the system propeties.
     * <p>
     * <p>The variable substitution delimeters are <b>${</b> and <b>}</b>.
     * <p>
     * <p>For example, if the System properties contains "key=value", then
     * the call
     * <pre>
     * String s = OptionConverter.substituteVars("Value of key is ${key}.");
     * </pre>
     * <p>
     * will set the variable <code>s</code> to "Value of key is value.".
     * <p>
     * <p>If no value could be found for the specified key, then the
     * <code>props</code> parameter is searched, if the value could not
     * be found there, then substitution defaults to the empty string.
     * <p>
     * <p>For example, if system propeties contains no value for the key
     * "inexistentKey", then the call
     * <p>
     * <pre>
     * String s = OptionConverter.subsVars("Value of inexistentKey is [${inexistentKey}]");
     * </pre>
     * will set <code>s</code> to "Value of inexistentKey is []"
     * <p>
     * <p>An {@link java.lang.IllegalArgumentException} is thrown if
     * <code>val</code> contains a start delimeter "${" which is not
     * balanced by a stop delimeter "}". </p>
     * <p>
     * <p><b>Author</b> Avy Sharell</a></p>
     *
     * @param val The string on which variable substitution is performed.
     * @throws IllegalArgumentException if <code>val</code> is malformed.
     */
    public static String substVars(String val, Properties props) throws IllegalArgumentException {

        StringBuffer sbuf = new StringBuffer();

        int i = 0;
        int j, k;

        while (true) {
            j = val.indexOf(DELIM_START, i);
            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return val;
                } else { // add the tail string which contails no variables and return the result.
                    sbuf.append(val.substring(i, val.length()));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(val.substring(i, j));
                k = val.indexOf(DELIM_STOP, j);
                if (k == -1) {
                    throw new IllegalArgumentException('"' + val +
                            "\" has no closing brace. Opening brace at position " + j
                            + '.');
                } else {
                    j += DELIM_START_LEN;
                    String key = val.substring(j, k);
                    // first try in System properties
                    String replacement = getSystemProperty(key, null);
                    // then try props parameter
                    if (replacement == null && props != null) {
                        replacement = props.getProperty(key);
                    }

                    if (replacement != null) {
                        // Do variable substitution on the replacement string
                        // such that we can solve "Hello ${x2}" as "Hello p1"
                        // the where the properties are
                        // x1=p1
                        // x2=${x1}
                        String recursiveReplacement = substVars(replacement, props);
                        sbuf.append(recursiveReplacement);
                    }
                    i = k + DELIM_STOP_LEN;
                }
            }
        }
    }

    /**
     * Configure log4j given an {@link InputStream}.
     * <p>
     * <p>
     * The InputStream will be interpreted by a new instance of a log4j configurator.
     * </p>
     * <p>
     * All configurations steps are taken on the <code>hierarchy</code> passed as a parameter.
     * </p>
     *
     * @param inputStream The configuration input stream.
     * @param clazz       The class name, of the log4j configurator which will parse the <code>inputStream</code>. This must be a
     *                    subclass of {@link Configurator}, or null. If this value is null then a default configurator of
     *                    {@link PropertyConfigurator} is used.
     * @param hierarchy   The {@link Hierarchy} to act on.
     * @since 1.2.17
     */


    public static void selectAndConfigure(InputStream inputStream, String clazz, LoggerRepository hierarchy) {
        Configurator configurator = null;

        if (clazz != null) {
            LogLog.debug("Preferred configurator class: " + clazz);
            configurator = (Configurator) instantiateByClassName(clazz,
                    Configurator.class,
                    null);
            if (configurator == null) {
                LogLog.error("Could not instantiate configurator [" + clazz + "].");
                return;
            }
        } else {
            configurator = new PropertyConfigurator();
        }

        configurator.doConfigure(inputStream, hierarchy);
    }

    //解析配置
    public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy) {
        Configurator configurator = null;
        String filename = url.getFile();

        //若配置文件是xml文件
        if (clazz == null && filename != null && filename.endsWith(".xml")) {
            clazz = "DomConfigurator";
        }

        //若类型名称不为空，则获取对应类型的配置器
        if (clazz != null) {
            LogLog.debug("Preferred configurator class: " + clazz);
            //获取对应类型的配置器
            configurator = (Configurator) instantiateByClassName(clazz, Configurator.class, null);
            if (configurator == null) {
                LogLog.error("Could not instantiate configurator [" + clazz + "].");
                return;
            }
            //若类型名为空，则新建属性文件配置器
        } else {
            configurator = new PropertyConfigurator();
        }
        //通过配置器解析文件
        configurator.doConfigure(url, hierarchy);
    }
}
