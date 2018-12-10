package com.liuyun.log4j;

import com.liuyun.log4j.spi.ThrowableRenderer;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

//增强异常渲染器
public final class EnhancedThrowableRenderer implements ThrowableRenderer {

    private Method getStackTraceMethod;    //Throwable.getStackTrace()方法
    private Method getClassNameMethod;     //StackTraceElement.getClassName()

    //构造器
    public EnhancedThrowableRenderer() {
        try {
            Class[] noArgs = null;
            getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
            Class ste = Class.forName("java.lang.StackTraceElement");
            getClassNameMethod = ste.getMethod("getClassName", noArgs);
        } catch (Exception ex) {
        }
    }

    //渲染方法
    public String[] doRender(final Throwable throwable) {
        if (getStackTraceMethod != null) {
            try {
                Object[] noArgs = null;
                Object[] elements = (Object[]) getStackTraceMethod.invoke(throwable, noArgs);
                String[] lines = new String[elements.length + 1];
                lines[0] = throwable.toString();
                Map classMap = new HashMap();
                for (int i = 0; i < elements.length; i++) {
                    lines[i + 1] = formatElement(elements[i], classMap);
                }
                return lines;
            } catch (Exception ex) {
            }
        }
        return DefaultThrowableRenderer.render(throwable);
    }

    //格式化元素
    private String formatElement(final Object element, final Map classMap) {
        StringBuffer buf = new StringBuffer("\tat ");
        buf.append(element);
        try {
            String className = getClassNameMethod.invoke(element, (Object[]) null).toString();
            Object classDetails = classMap.get(className);
            if (classDetails != null) {
                buf.append(classDetails);
            } else {
                Class cls = findClass(className);
                int detailStart = buf.length();
                buf.append('[');
                try {
                    CodeSource source = cls.getProtectionDomain().getCodeSource();
                    if (source != null) {
                        URL locationURL = source.getLocation();
                        if (locationURL != null) {
                            //
                            //   if a file: URL
                            //
                            if ("file".equals(locationURL.getProtocol())) {
                                String path = locationURL.getPath();
                                if (path != null) {
                                    //
                                    //  find the last file separator character
                                    //
                                    int lastSlash = path.lastIndexOf('/');
                                    int lastBack = path.lastIndexOf(File.separatorChar);
                                    if (lastBack > lastSlash) {
                                        lastSlash = lastBack;
                                    }
                                    //
                                    //  if no separator or ends with separator (a directory)
                                    //     then output the URL, otherwise just the file name.
                                    //
                                    if (lastSlash <= 0 || lastSlash == path.length() - 1) {
                                        buf.append(locationURL);
                                    } else {
                                        buf.append(path.substring(lastSlash + 1));
                                    }
                                }
                            } else {
                                buf.append(locationURL);
                            }
                        }
                    }
                } catch (SecurityException ex) {
                }
                buf.append(':');
                Package pkg = cls.getPackage();
                if (pkg != null) {
                    String implVersion = pkg.getImplementationVersion();
                    if (implVersion != null) {
                        buf.append(implVersion);
                    }
                }
                buf.append(']');
                classMap.put(className, buf.substring(detailStart));
            }
        } catch (Exception ex) {
        }
        return buf.toString();
    }

    //根据名称寻找类型
    private Class findClass(final String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e1) {
                return getClass().getClassLoader().loadClass(className);
            }
        }
    }

}
