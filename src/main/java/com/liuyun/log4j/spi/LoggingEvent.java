package com.liuyun.log4j.spi;

import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.liuyun.log4j.Level;
import com.liuyun.log4j.Category;
import com.liuyun.log4j.MDC;
import com.liuyun.log4j.NDC;
import com.liuyun.log4j.Priority;
import com.liuyun.log4j.helpers.Loader;
import com.liuyun.log4j.helpers.LogLog;

//日志事件
public class LoggingEvent implements java.io.Serializable {

    private static long startTime = System.currentTimeMillis();

    /**
     * Fully qualified name of the calling category class.
     */
    transient public final String fqnOfCategoryClass;

    //日志类
    transient private Category logger;

    //日志名
    final public String categoryName;

    //日志事件水平
    transient public Priority level;

    /**
     * The nested diagnostic context (NDC) of logging event.
     */
    private String ndc;

    /**
     * The mapped diagnostic context (MDC) of logging event.
     */
    private Hashtable mdcCopy;


    /**
     * Have we tried to do an NDC lookup? If we did, there is no need
     * to do it again.  Note that its value is always false when
     * serialized. Thus, a receiving SocketNode will never use it's own
     * (incorrect) NDC. See also writeObject method.
     */
    private boolean ndcLookupRequired = true;


    /**
     * Have we tried to do an MDC lookup? If we did, there is no need
     * to do it again.  Note that its value is always false when
     * serialized. See also the getMDC and getMDCCopy methods.
     */
    private boolean mdcCopyLookupRequired = true;

    //日志事件消息
    transient private Object message;

    //渲染消息
    private String renderedMessage;

    //线程名
    private String threadName;

    //异常信息
    private ThrowableInformation throwableInfo;

    //日志事件时间戳
    public final long timeStamp;

    //调用者位置信息
    private LocationInfo locationInfo;

    //序列化ID
    static final long serialVersionUID = -868428216207166145L;

    static final Integer[] PARAM_ARRAY = new Integer[1];
    static final String TO_LEVEL = "toLevel";
    static final Class[] TO_LEVEL_PARAMS = new Class[]{int.class};
    static final Hashtable methodCache = new Hashtable(3); // use a tiny table

    //构造器
    public LoggingEvent(String fqnOfCategoryClass, Category logger,
                        Priority level, Object message, Throwable throwable) {
        this.fqnOfCategoryClass = fqnOfCategoryClass;
        this.logger = logger;
        this.categoryName = logger.getName();
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable, logger);
        }
        timeStamp = System.currentTimeMillis();
    }

    //构造器
    public LoggingEvent(String fqnOfCategoryClass, Category logger,
                        long timeStamp, Priority level, Object message,
                        Throwable throwable) {
        this.fqnOfCategoryClass = fqnOfCategoryClass;
        this.logger = logger;
        this.categoryName = logger.getName();
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable, logger);
        }

        this.timeStamp = timeStamp;
    }

    //构造器
    public LoggingEvent(final String fqnOfCategoryClass,
                        final Category logger,
                        final long timeStamp,
                        final Level level,
                        final Object message,
                        final String threadName,
                        final ThrowableInformation throwable,
                        final String ndc,
                        final LocationInfo info,
                        final java.util.Map properties) {
        super();
        this.fqnOfCategoryClass = fqnOfCategoryClass;
        this.logger = logger;
        if (logger != null) {
            categoryName = logger.getName();
        } else {
            categoryName = null;
        }
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = throwable;
        }

        this.timeStamp = timeStamp;
        this.threadName = threadName;
        ndcLookupRequired = false;
        this.ndc = ndc;
        this.locationInfo = info;
        mdcCopyLookupRequired = false;
        if (properties != null) {
            mdcCopy = new java.util.Hashtable(properties);
        }
    }


    /**
     * Set the location information for this logging event. The collected
     * information is cached for future use.
     */
    public LocationInfo getLocationInformation() {
        if (locationInfo == null) {
            locationInfo = new LocationInfo(new Throwable(), fqnOfCategoryClass);
        }
        return locationInfo;
    }

    //获取日志级别
    public Level getLevel() {
        return (Level) level;
    }

    //获取日志名
    public String getLoggerName() {
        return categoryName;
    }

    //获取日志实例
    public Category getLogger() {
        return logger;
    }

    //获取消息对象
    public Object getMessage() {
        if (message != null) {
            return message;
        } else {
            return getRenderedMessage();
        }
    }

    /**
     * This method returns the NDC for this event. It will return the
     * correct content even if the event was generated in a different
     * thread or even on a different machine. The {@link NDC#get} method
     * should <em>never</em> be called directly.
     */
    public String getNDC() {
        if (ndcLookupRequired) {
            ndcLookupRequired = false;
            ndc = NDC.get();
        }
        return ndc;
    }


    /**
     * Returns the the context corresponding to the <code>key</code>
     * parameter. If there is a local MDC copy, possibly because we are
     * in a logging server or running inside AsyncAppender, then we
     * search for the key in MDC copy, if a value is found it is
     * returned. Otherwise, if the search in MDC copy returns a null
     * result, then the current thread's <code>MDC</code> is used.
     *
     * <p>Note that <em>both</em> the local MDC copy and the current
     * thread's MDC are searched.
     */
    public Object getMDC(String key) {
        Object r;
        // Note the mdcCopy is used if it exists. Otherwise we use the MDC
        // that is associated with the thread.
        if (mdcCopy != null) {
            r = mdcCopy.get(key);
            if (r != null) {
                return r;
            }
        }
        return MDC.get(key);
    }

    /**
     * Obtain a copy of this thread's MDC prior to serialization or
     * asynchronous logging.
     */
    public void getMDCCopy() {
        if (mdcCopyLookupRequired) {
            mdcCopyLookupRequired = false;
            // the clone call is required for asynchronous logging.
            // See also bug #5932.
            Hashtable t = MDC.getContext();
            if (t != null) {
                mdcCopy = (Hashtable) t.clone();
            }
        }
    }

    //获取渲染后的消息
    public String getRenderedMessage() {
        if (renderedMessage == null && message != null) {
            if (message instanceof String) {
                renderedMessage = (String) message;
            } else {
                LoggerRepository repository = logger.getLoggerRepository();

                if (repository instanceof RendererSupport) {
                    RendererSupport rs = (RendererSupport) repository;
                    renderedMessage = rs.getRendererMap().findAndRender(message);
                } else {
                    renderedMessage = message.toString();
                }
            }
        }
        return renderedMessage;
    }

    //获取开始时间
    public static long getStartTime() {
        return startTime;
    }

    //获取线程名
    public String getThreadName() {
        if (threadName == null) {
            threadName = (Thread.currentThread()).getName();
        }
        return threadName;
    }

    //获取异常信息
    public ThrowableInformation getThrowableInformation() {
        return throwableInfo;
    }

    /**
     * Return this event's throwable's string[] representaion.
     */
    public String[] getThrowableStrRep() {

        if (throwableInfo == null) {
            return null;
        } else {
            return throwableInfo.getThrowableStrRep();
        }
    }


    private void readLevel(ObjectInputStream ois)
            throws java.io.IOException, ClassNotFoundException {

        int p = ois.readInt();
        try {
            String className = (String) ois.readObject();
            if (className == null) {
                level = Level.toLevel(p);
            } else {
                Method m = (Method) methodCache.get(className);
                if (m == null) {
                    Class clazz = Loader.loadClass(className);
                    // Note that we use Class.getDeclaredMethod instead of
                    // Class.getMethod. This assumes that the Level subclass
                    // implements the toLevel(int) method which is a
                    // requirement. Actually, it does not make sense for Level
                    // subclasses NOT to implement this method. Also note that
                    // only Level can be subclassed and not Priority.
                    m = clazz.getDeclaredMethod(TO_LEVEL, TO_LEVEL_PARAMS);
                    methodCache.put(className, m);
                }
                level = (Level) m.invoke(null, new Integer[]{new Integer(p)});
            }
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof InterruptedException
                    || e.getTargetException() instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.warn("Level deserialization failed, reverting to default.", e);
            level = Level.toLevel(p);
        } catch (NoSuchMethodException e) {
            LogLog.warn("Level deserialization failed, reverting to default.", e);
            level = Level.toLevel(p);
        } catch (IllegalAccessException e) {
            LogLog.warn("Level deserialization failed, reverting to default.", e);
            level = Level.toLevel(p);
        } catch (RuntimeException e) {
            LogLog.warn("Level deserialization failed, reverting to default.", e);
            level = Level.toLevel(p);
        }
    }

    //读对象
    private void readObject(ObjectInputStream ois)
            throws java.io.IOException, ClassNotFoundException {
        ois.defaultReadObject();
        readLevel(ois);

        // Make sure that no location info is available to Layouts
        if (locationInfo == null) {
            locationInfo = new LocationInfo(null, null);
        }
    }

    //写对象
    private void writeObject(ObjectOutputStream oos) throws java.io.IOException {
        // Aside from returning the current thread name the wgetThreadName
        // method sets the threadName variable.
        this.getThreadName();

        // This sets the renders the message in case it wasn't up to now.
        this.getRenderedMessage();

        // This call has a side effect of setting this.ndc and
        // setting ndcLookupRequired to false if not already false.
        this.getNDC();

        // This call has a side effect of setting this.mdcCopy and
        // setting mdcLookupRequired to false if not already false.
        this.getMDCCopy();

        // This sets the throwable sting representation of the event throwable.
        this.getThrowableStrRep();

        oos.defaultWriteObject();

        // serialize this event's level
        writeLevel(oos);
    }

    private void writeLevel(ObjectOutputStream oos) throws java.io.IOException {

        oos.writeInt(level.toInt());

        Class clazz = level.getClass();
        if (clazz == Level.class) {
            oos.writeObject(null);
        } else {
            // writing directly the Class object would be nicer, except that
            // serialized a Class object can not be read back by JDK
            // 1.1.x. We have to resort to this hack instead.
            oos.writeObject(clazz.getName());
        }
    }

    /**
     * Set value for MDC property.
     * This adds the specified MDC property to the event.
     * Access to the MDC is not synchronized, so this
     * method should only be called when it is known that
     * no other threads are accessing the MDC.
     *
     * @param propName
     * @param propValue
     * @since 1.2.15
     */
    public final void setProperty(final String propName,
                                  final String propValue) {
        if (mdcCopy == null) {
            getMDCCopy();
        }
        if (mdcCopy == null) {
            mdcCopy = new Hashtable();
        }
        mdcCopy.put(propName, propValue);
    }

    /**
     * Return a property for this event. The return value can be null.
     * <p>
     * Equivalent to getMDC(String) in log4j 1.2.  Provided
     * for compatibility with log4j 1.3.
     *
     * @param key property name
     * @return property value or null if property not set
     * @since 1.2.15
     */
    public final String getProperty(final String key) {
        Object value = getMDC(key);
        String retval = null;
        if (value != null) {
            retval = value.toString();
        }
        return retval;
    }

    /**
     * Check for the existence of location information without creating it
     * (a byproduct of calling getLocationInformation).
     *
     * @return true if location information has been extracted.
     * @since 1.2.15
     */
    public final boolean locationInformationExists() {
        return (locationInfo != null);
    }

    //获取时间戳
    public final long getTimeStamp() {
        return timeStamp;
    }

    //获取所有属性名称
    public Set getPropertyKeySet() {
        return getProperties().keySet();
    }

    //获取所有属性映射
    public Map getProperties() {
        getMDCCopy();
        Map properties;
        if (mdcCopy == null) {
            properties = new HashMap();
        } else {
            properties = mdcCopy;
        }
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Get the fully qualified name of the calling logger sub-class/wrapper.
     * Provided for compatibility with log4j 1.3
     *
     * @return fully qualified class name, may be null.
     * @since 1.2.15
     */
    public String getFQNOfLoggerClass() {
        return fqnOfCategoryClass;
    }


    //移除属性
    public Object removeProperty(String propName) {
        if (mdcCopy == null) {
            getMDCCopy();
        }
        if (mdcCopy == null) {
            mdcCopy = new Hashtable();
        }
        return mdcCopy.remove(propName);
    }
}
