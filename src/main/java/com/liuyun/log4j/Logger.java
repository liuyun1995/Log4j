package com.liuyun.log4j;

import com.liuyun.log4j.spi.LoggerFactory;

//日志类
public class Logger extends Category {

    //日志类全限定名
    private static final String FQCN = Logger.class.getName();

    //构造器
    protected Logger(String name) {
        super(name);
    }

    //根据名称获取日志类
    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    //根据类型获取日志类
    public static Logger getLogger(Class clazz) {
        return LogManager.getLogger(clazz.getName());
    }

    //获取根日志类
    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    //根据名称和工厂获取日志类
    public static Logger getLogger(String name, LoggerFactory factory) {
        return LogManager.getLogger(name, factory);
    }

    //打印trace级别日志
    public void trace(Object message) {
        if (repository.isDisabled(Level.TRACE_INT)) {
            return;
        }
        if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.TRACE, message, null);
        }
    }

    //打印trace级别日志
    public void trace(Object message, Throwable t) {
        if (repository.isDisabled(Level.TRACE_INT)) {
            return;
        }
        if (Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
            forcedLog(FQCN, Level.TRACE, message, t);
        }
    }

    //是否trace级别开启
    public boolean isTraceEnabled() {
        if (repository.isDisabled(Level.TRACE_INT)) {
            return false;
        }
        return Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel());
    }

}
