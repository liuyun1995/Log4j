package com.liuyun.log4j;

import com.liuyun.log4j.helpers.OptionConverter;
import com.liuyun.log4j.spi.LoggerFactory;
import com.liuyun.log4j.spi.RootLogger;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.RepositorySelector;
import com.liuyun.log4j.spi.DefaultRepositorySelector;
import com.liuyun.log4j.spi.NOPLoggerRepository;
import com.liuyun.log4j.helpers.Loader;
import com.liuyun.log4j.helpers.LogLog;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.io.StringWriter;
import java.io.PrintWriter;

//日志管理器
public class LogManager {

    //默认配置文件
    static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
    /**
     * @deprecated This variable is for internal use only. It will
     * become private in future versions.
     */
    static final public String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
    /**
     * @deprecated This variable is for internal use only. It will
     * become private in future versions.
     */
    static final public String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
    /**
     * @deprecated This variable is for internal use only. It will
     * become private in future versions.
     */
    public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";

    //默认xml配置文件
    static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
    /**
     * private，私有的访问权限，也是最严格的访问权限，仅只能在设置了该权限的类中访问，利用这个访问权限，表现出封装思想。
     * <p>
     * default，默认的访问权限，也是可以省略的访问权限，它不仅能在设置了该权限的类中访问，也可以在同一包中的类或子类中访问。
     * <p>
     * protected，受保护的访问权限，它除了具有default的访问权限外，还可以在不同包中所继承的子类访问。
     * <p>
     * public，公有的访问权限，也是最宽松的访问权限，不仅可以是同一个类或子类，还是同一个包中的类或子类，又还是不同包中的类或子类，都可以访问。
     */
    static private Object guard = null;
    static private RepositorySelector repositorySelector;

    //log4j的入口
    static {
        //记录日志的等级
        Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
        //新建日志容器选择器
        repositorySelector = new DefaultRepositorySelector(h);

        /** Search for the properties file log4j.properties in the CLASSPATH.  */
        String override = OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY, null);

        // if there is no default init override, then get the resource
        // specified by the user or the default config file.
        if (override == null || "false".equalsIgnoreCase(override)) {
            //获取log4j.configuration文件
            String configurationOptionStr = OptionConverter.getSystemProperty(DEFAULT_CONFIGURATION_KEY, null);
            //获取log4j.configuratorClass文件
            String configuratorClassName = OptionConverter.getSystemProperty(CONFIGURATOR_CLASS_KEY, null);

            URL url = null;

            //若log4j.configuration文件不存在
            if (configurationOptionStr == null) {
                //先读取log4j.xml文件
                url = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);
                //若log4j.xml文件为空，则读取log4j.properties文件
                if (url == null) {
                    url = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
                }
            //若log4j.configuration文件存在
            } else {
                try {
                    //新建URL对象
                    url = new URL(configurationOptionStr);
                } catch (MalformedURLException ex) {
                    //若该资源不是URL，则尝试从类路径下获取
                    url = Loader.getResource(configurationOptionStr);
                }
            }

            // If we have a non-null url, then delegate the rest of the
            // configuration to the OptionConverter.selectAndConfigure
            // method.
            if (url != null) {
                LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");
                try {
                    OptionConverter.selectAndConfigure(url, configuratorClassName, LogManager.getLoggerRepository());
                } catch (NoClassDefFoundError e) {
                    LogLog.warn("Error during default initialization", e);
                }
            } else {
                LogLog.debug("Could not find resource: [" + configurationOptionStr + "].");
            }
        } else {
            LogLog.debug("Default initialization of overridden by " + DEFAULT_INIT_OVERRIDE_KEY + "property.");
        }
    }

    /**
     * Sets <code>LoggerFactory</code> but only if the correct
     * <em>guard</em> is passed as parameter.
     * <p>
     * <p>Initally the guard is null.  If the guard is
     * <code>null</code>, then invoking this method sets the logger
     * factory and the guard. Following invocations will throw a {@link
     * IllegalArgumentException}, unless the previously set
     * <code>guard</code> is passed as the second parameter.
     * <p>
     * <p>This allows a high-level component to set the {@link
     * RepositorySelector} used by the <code>LogManager</code>.
     * <p>
     * <p>For example, when tomcat starts it will be able to install its
     * own repository selector. However, if and when Tomcat is embedded
     * within JBoss, then JBoss will install its own repository selector
     * and Tomcat will use the repository selector set by its container,
     * JBoss.
     */

    public static void setRepositorySelector(RepositorySelector selector, Object guard) throws IllegalArgumentException {
        if ((LogManager.guard != null) && (LogManager.guard != guard)) {
            throw new IllegalArgumentException(
                    "Attempted to reset the LoggerFactory without possessing the guard.");
        }

        if (selector == null) {
            throw new IllegalArgumentException("RepositorySelector must be non-null.");
        }

        LogManager.guard = guard;
        LogManager.repositorySelector = selector;
    }


    /**
     * This method tests if called from a method that
     * is known to result in class members being abnormally
     * set to null but is assumed to be harmless since the
     * all classes are in the process of being unloaded.
     *
     * @param ex exception used to determine calling stack.
     * @return true if calling stack is recognized as likely safe.
     */
    private static boolean isLikelySafeScenario(final Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String msg = stringWriter.toString();
        return msg.indexOf("org.apache.catalina.loader.WebappClassLoader.stop") != -1;
    }

    //获取日志对象容器
    public static LoggerRepository getLoggerRepository() {
        if (repositorySelector == null) {
            repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
            guard = null;
            Exception ex = new IllegalStateException("Class invariant violation");
            String msg = "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
            if (isLikelySafeScenario(ex)) {
                LogLog.debug(msg, ex);
            } else {
                LogLog.error(msg, ex);
            }
        }
        return repositorySelector.getLoggerRepository();
    }

    //获取根级日志对象
    public static Logger getRootLogger() {
        return getLoggerRepository().getRootLogger();
    }

    //根据名称获取日志对象
    public static Logger getLogger(final String name) {
        return getLoggerRepository().getLogger(name);
    }

    //根据类型获取日志对象
    public static Logger getLogger(final Class clazz) {
        return getLoggerRepository().getLogger(clazz.getName());
    }

    //根据名称和工厂获取日志对象
    public static Logger getLogger(final String name, final LoggerFactory factory) {
        return getLoggerRepository().getLogger(name, factory);
    }

    //是否存在对应的日志对象
    public static Logger exists(final String name) {
        return getLoggerRepository().exists(name);
    }

    //获取当前日志对象
    public static Enumeration getCurrentLoggers() {
        return getLoggerRepository().getCurrentLoggers();
    }

    //关闭日志
    public static void shutdown() {
        getLoggerRepository().shutdown();
    }

    //重置配置信息
    public static void resetConfiguration() {
        getLoggerRepository().resetConfiguration();
    }

}

