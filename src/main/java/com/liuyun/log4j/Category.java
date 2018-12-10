package com.liuyun.log4j;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.appender.AsyncAppender;
import com.liuyun.log4j.appender.net.SocketAppender;
import com.liuyun.log4j.helpers.AppenderAttachableImpl;
import com.liuyun.log4j.helpers.NullEnumeration;
import com.liuyun.log4j.or.ObjectRenderer;
import com.liuyun.log4j.spi.AppenderAttachable;
import com.liuyun.log4j.spi.HierarchyEventListener;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.LoggingEvent;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

//日志类型
public class Category implements AppenderAttachable {

    //日志类型全限定名
    private static final String FQCN = Category.class.getName();
    //日志类型名称
    protected String name;
    //日志级别
    volatile protected Level level;
    //父级日志类型
    volatile protected Category parent;
    //资源绑定
    protected ResourceBundle resourceBundle;
    //日志容器
    protected LoggerRepository repository;
    /**
     * Additivity is set to true by default, that is children inherit
     * the appenders of their ancestors by default. If this variable is
     * set to <code>false</code> then the appenders found in the
     * ancestors of this category are not used. However, the children
     * of this category will inherit its appenders, unless the children
     * have their additivity flag set to <code>false</code> too. See
     * the user manual for more details.
     */
    protected boolean additive = true;
    AppenderAttachableImpl aai;


    protected Category(String name) {
        this.name = name;
    }

    public static Logger exists(String name) {
        return LogManager.exists(name);
    }

    //获取当前类型
    public static Enumeration getCurrentCategories() {
        return LogManager.getCurrentLoggers();
    }

    //获取默认日志容器
    public static LoggerRepository getDefaultHierarchy() {
        return LogManager.getLoggerRepository();
    }

    //根据名称获取类型实例
    public static Category getInstance(String name) {
        return LogManager.getLogger(name);
    }

    //根据类型获取类型实例
    public static Category getInstance(Class clazz) {
        return LogManager.getLogger(clazz);
    }

    //获取根级日志类型
    public static final Category getRoot() {
        return LogManager.getRootLogger();
    }

    //关闭日志
    public static void shutdown() {
        LogManager.shutdown();
    }

    //添加输出器
    synchronized public void addAppender(Appender newAppender) {
        if (aai == null) {
            aai = new AppenderAttachableImpl();
        }
        aai.addAppender(newAppender);
        repository.fireAddAppenderEvent(this, newAppender);
    }

    /**
     * If <code>assertion</code> parameter is <code>false</code>, then
     * logs <code>msg</code> as an {@link #error(Object) error} statement.
     * <p>
     * <p>The <code>assert</code> method has been renamed to
     * <code>assertLog</code> because <code>assert</code> is a language
     * reserved word in JDK 1.4.
     *
     * @param assertion
     * @param msg       The message to print if <code>assertion</code> is
     *                  false.
     * @since 1.2
     */
    public void assertLog(boolean assertion, String msg) {
        if (!assertion) {
            this.error(msg);
        }
    }

    //调用所有输出源
    public void callAppenders(LoggingEvent event) {
        int writes = 0;

        //从子类型向上遍历
        for (Category c = this; c != null; c = c.parent) {
            // Protected against simultaneous call to addAppender, removeAppender,...
            synchronized (c) {
                if (c.aai != null) {
                    writes += c.aai.appendLoopOnAppenders(event);
                }
                if (!c.additive) {
                    break;
                }
            }
        }

        if (writes == 0) {
            repository.emitNoAppenderWarning(this);
        }
    }

    //关闭嵌套的输出器
    synchronized void closeNestedAppenders() {
        Enumeration enumeration = this.getAllAppenders();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Appender a = (Appender) enumeration.nextElement();
                if (a instanceof AppenderAttachable) {
                    a.close();
                }
            }
        }
    }


    /**
     * This method creates a new logging event and logs the event
     * without further checks.
     */
    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        callAppenders(new LoggingEvent(fqcn, this, level, message, t));
    }

    /**
     * Get the additivity flag for this Category instance.
     */
    public boolean getAdditivity() {
        return additive;
    }

    /**
     * Set the additivity flag for this Category instance.
     *
     * @since 0.8.1
     */
    public void setAdditivity(boolean additive) {
        this.additive = additive;
    }

    //获取所有输出源
    synchronized public Enumeration getAllAppenders() {
        if (aai == null) {
            return NullEnumeration.getInstance();
        } else {
            return aai.getAllAppenders();
        }
    }

    //获取指定名称输出源
    synchronized public Appender getAppender(String name) {
        if (aai == null || name == null) {
            return null;
        }
        return aai.getAppender(name);
    }

    //获取有效的日志级别
    public Level getEffectiveLevel() {
        for (Category c = this; c != null; c = c.parent) {
            if (c.level != null) {
                return c.level;
            }
        }
        return null;
    }

    //获取有效的日志级别
    public Priority getChainedPriority() {
        for (Category c = this; c != null; c = c.parent) {
            if (c.level != null) {
                return c.level;
            }
        }
        return null;
    }

    //获取日志容器
    public LoggerRepository getHierarchy() {
        return repository;
    }

    //设置日志容器
    final void setHierarchy(LoggerRepository repository) {
        this.repository = repository;
    }

    //获取日志容器
    public LoggerRepository getLoggerRepository() {
        return repository;
    }

    //获取类型名称
    public final String getName() {
        return name;
    }

    //获取父级类型
    public final Category getParent() {
        return this.parent;
    }

    //获取日志级别
    public final Level getLevel() {
        return this.level;
    }

    //设置日志级别
    public void setLevel(Level level) {
        this.level = level;
    }

    //获取日志级别
    public final Level getPriority() {
        return this.level;
    }

    //设置日志级别
    public void setPriority(Priority priority) {
        this.level = (Level) priority;
    }

    //获取绑定的资源
    public ResourceBundle getResourceBundle() {
        for (Category c = this; c != null; c = c.parent) {
            if (c.resourceBundle != null) {
                return c.resourceBundle;
            }
        }
        // It might be the case that there is no resource bundle
        return null;
    }

    //设置绑定的资源
    public void setResourceBundle(ResourceBundle bundle) {
        resourceBundle = bundle;
    }

    //获取绑定资源字符串
    protected String getResourceBundleString(String key) {
        ResourceBundle rb = getResourceBundle();
        // This is one of the rare cases where we can use logging in order
        // to report errors from within log4j.
        if (rb == null) {
            //if(!hierarchy.emittedNoResourceBundleWarning) {
            //error("No resource bundle has been set for category "+name);
            //hierarchy.emittedNoResourceBundleWarning = true;
            //}
            return null;
        } else {
            try {
                return rb.getString(key);
            } catch (MissingResourceException mre) {
                error("No resource is associated with key \"" + key + "\".");
                return null;
            }
        }
    }

    //是否可附着的
    public boolean isAttached(Appender appender) {
        if (appender == null || aai == null) {
            return false;
        } else {
            return aai.isAttached(appender);
        }
    }


    public boolean isDebugEnabled() {
        if (repository.isDisabled(Level.DEBUG_INT)) {
            return false;
        }
        return Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel());
    }

    public boolean isEnabledFor(Priority level) {
        if (repository.isDisabled(level.level)) {
            return false;
        }
        return level.isGreaterOrEqual(this.getEffectiveLevel());
    }

    public boolean isInfoEnabled() {
        if (repository.isDisabled(Level.INFO_INT)) {
            return false;
        }
        return Level.INFO.isGreaterOrEqual(this.getEffectiveLevel());
    }


    public void l7dlog(Priority priority, String key, Throwable t) {
        if (repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            String msg = getResourceBundleString(key);
            // if message corresponding to 'key' could not be found in the
            // resource bundle, then default to 'key'.
            if (msg == null) {
                msg = key;
            }
            forcedLog(FQCN, priority, msg, t);
        }
    }

    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        if (repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            String pattern = getResourceBundleString(key);
            String msg;
            if (pattern == null) {
                msg = key;
            } else {
                msg = java.text.MessageFormat.format(pattern, params);
            }
            forcedLog(FQCN, priority, msg, t);
        }
    }

    //打印日志方法
    public void log(Priority priority, Object message, Throwable t) {
        if (repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, priority, message, t);
        }
    }

    //打印日志方法
    public void log(Priority priority, Object message) {
        if (repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, priority, message, null);
        }
    }

    //打印日志方法
    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
        if (repository.isDisabled(level.level)) {
            return;
        }
        if (level.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(callerFQCN, level, message, t);
        }
    }

    //移除删除输出源事件
    private void fireRemoveAppenderEvent(final Appender appender) {
        if (appender != null) {
            if (repository instanceof Hierarchy) {
                ((Hierarchy) repository).fireRemoveAppenderEvent(this, appender);
            } else if (repository instanceof HierarchyEventListener) {
                ((HierarchyEventListener) repository).removeAppenderEvent(this, appender);
            }
        }
    }

    //移除全部输出源
    synchronized public void removeAllAppenders() {
        if (aai != null) {
            Vector appenders = new Vector();
            for (Enumeration iter = aai.getAllAppenders(); iter != null && iter.hasMoreElements(); ) {
                appenders.add(iter.nextElement());
            }
            aai.removeAllAppenders();
            for (Enumeration iter = appenders.elements(); iter.hasMoreElements(); ) {
                fireRemoveAppenderEvent((Appender) iter.nextElement());
            }
            aai = null;
        }
    }

    //移除指定输出源
    synchronized public void removeAppender(Appender appender) {
        if (appender == null || aai == null) {
            return;
        }
        boolean wasAttached = aai.isAttached(appender);
        aai.removeAppender(appender);
        if (wasAttached) {
            fireRemoveAppenderEvent(appender);
        }
    }

    //移除指定名称输出源
    synchronized public void removeAppender(String name) {
        if (name == null || aai == null) {
            return;
        }
        Appender appender = aai.getAppender(name);
        aai.removeAppender(name);
        if (appender != null) {
            fireRemoveAppenderEvent(appender);
        }
    }

    //-------------------------------------------------打印不同级别的日志----------------------------------------------

    public void debug(Object message) {
        if (repository.isDisabled(Level.DEBUG_INT)) {
            return;
        }
        if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.DEBUG, message, null);
        }
    }


    public void debug(Object message, Throwable t) {
        if (repository.isDisabled(Level.DEBUG_INT)) {
            return;
        }
        if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.DEBUG, message, t);
        }
    }

    public void info(Object message) {
        if (repository.isDisabled(Level.INFO_INT)) {
            return;
        }
        Level effectivelevel = this.getEffectiveLevel(); //实际的日志等级
        if (Level.INFO.isGreaterOrEqual(effectivelevel)) {
            forcedLog(FQCN, Level.INFO, message, null);
        }
    }

    public void info(Object message, Throwable t) {
        if (repository.isDisabled(Level.INFO_INT)) {
            return;
        }
        if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.INFO, message, t);
        }
    }

    public void error(Object message) {
        if (repository.isDisabled(Level.ERROR_INT)) {
            return;
        }
        if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.ERROR, message, null);
        }
    }

    public void error(Object message, Throwable t) {
        if (repository.isDisabled(Level.ERROR_INT)) {
            return;
        }
        if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.ERROR, message, t);
        }

    }

    public void fatal(Object message) {
        if (repository.isDisabled(Level.FATAL_INT)) {
            return;
        }
        if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.FATAL, message, null);
        }
    }

    public void fatal(Object message, Throwable t) {
        if (repository.isDisabled(Level.FATAL_INT)) {
            return;
        }
        if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.FATAL, message, t);
        }
    }

    public void warn(Object message) {
        if (repository.isDisabled(Level.WARN_INT)) {
            return;
        }
        if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.WARN, message, null);
        }
    }

    public void warn(Object message, Throwable t) {
        if (repository.isDisabled(Level.WARN_INT)) {
            return;
        }
        if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.WARN, message, t);
        }
    }

}
