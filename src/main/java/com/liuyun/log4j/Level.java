package com.liuyun.log4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
每个Logger都被了一个日志级别（log level），用来控制日志信息的输出。日志级别从高到低分为：
A：off         最高等级，用于关闭所有日志记录。
B：fatal       指出每个严重的错误事件将会导致应用程序的退出。
C：error      指出虽然发生错误事件，但仍然不影响系统的继续运行。
D：warm     表明会出现潜在的错误情形。
E：info         一般和在粗粒度级别上，强调应用程序的运行全程。
F：debug     一般用于细粒度级别上，对调试应用程序非常有帮助。
G：all           最低等级，用于打开所有日志记录。
og4j只建议使用4个级别，优先级从高到低分别是 error,warn,info和debug。
通过使用日志级别，可以控制应用程序中相应级别日志信息的输出。
例如，如果使用了info级别，则应用程 序中所有低于info级别的日志信息(如debug)将不会被打印出来。
 */
public class Level extends Priority implements Serializable {

    static final long serialVersionUID = 3491141966387921974L;

    private static final String ALL_NAME = "ALL";
    private static final String TRACE_NAME = "TRACE";
    private static final String DEBUG_NAME = "DEBUG";
    private static final String INFO_NAME = "INFO";
    private static final String WARN_NAME = "WARN";
    private static final String ERROR_NAME = "ERROR";
    private static final String FATAL_NAME = "FATAL";
    private static final String OFF_NAME = "OFF";

    public static final int TRACE_INT = 5000;
    final static public Level OFF = new Level(OFF_INT, OFF_NAME, 0);
    final static public Level FATAL = new Level(FATAL_INT, FATAL_NAME, 0);
    final static public Level ERROR = new Level(ERROR_INT, ERROR_NAME, 3);
    final static public Level WARN = new Level(WARN_INT, WARN_NAME, 4);
    final static public Level INFO = new Level(INFO_INT, INFO_NAME, 6);
    final static public Level DEBUG = new Level(DEBUG_INT, DEBUG_NAME, 7);
    public static final Level TRACE = new Level(TRACE_INT, TRACE_NAME, 7);
    final static public Level ALL = new Level(ALL_INT, ALL_NAME, 7);

    //构造器
    protected Level(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    //根据名称获取水平
    public static Level toLevel(String sArg) {
        return toLevel(sArg, Level.DEBUG);
    }

    //根据代码获取水平
    public static Level toLevel(int val) {
        return toLevel(val, Level.DEBUG);
    }

    //根据代码获取水平
    public static Level toLevel(int val, Level defaultLevel) {
        switch (val) {
        case ALL_INT:
            return ALL;
        case DEBUG_INT:
            return Level.DEBUG;
        case INFO_INT:
            return Level.INFO;
        case WARN_INT:
            return Level.WARN;
        case ERROR_INT:
            return Level.ERROR;
        case FATAL_INT:
            return Level.FATAL;
        case OFF_INT:
            return OFF;
        case TRACE_INT:
            return Level.TRACE;
        default:
            return defaultLevel;
        }
    }

    //根据名称获取水平
    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg == null) {
            return defaultLevel;
        }
        String s = sArg.toUpperCase();

        if (s.equals(ALL_NAME)) {
            return Level.ALL;
        }
        if (s.equals(DEBUG_NAME)) {
            return Level.DEBUG;
        }
        if (s.equals(INFO_NAME)) {
            return Level.INFO;
        }
        if (s.equals(WARN_NAME)) {
            return Level.WARN;
        }
        if (s.equals(ERROR_NAME)) {
            return Level.ERROR;
        }
        if (s.equals(FATAL_NAME)) {
            return Level.FATAL;
        }
        if (s.equals(OFF_NAME)) {
            return Level.OFF;
        }
        if (s.equals(TRACE_NAME)) {
            return Level.TRACE;
        }
        if (s.equals("\u0130NFO")) {
            return Level.INFO;
        }
        return defaultLevel;
    }

    //读对象
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        level = s.readInt();
        syslogEquivalent = s.readInt();
        levelStr = s.readUTF();
        if (levelStr == null) {
            levelStr = "";
        }
    }

    //写对象
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(level);
        s.writeInt(syslogEquivalent);
        s.writeUTF(levelStr);
    }

    private Object readResolve() throws ObjectStreamException {
        if (getClass() == Level.class) {
            return toLevel(level);
        }
        return this;
    }

}
