/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Contributors:  Kitching Simon <Simon.Kitching@orange.ch>
//                Nicholas Wolff

package com.jd.log4j;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Defines the minimum set of levels recognized by the system, that is <code>OFF</code>, <code>FATAL</code>, <code>ERROR</code>,
 * <code>WARN</code>, <code>INFO</code>, <code>DEBUG</code> and <code>ALL</code>.
 * 
 * <p>
 * The <code>Level</code> class may be subclassed to define a larger level set.
 * </p>
 * 
 * @author Ceki G&uuml;lc&uuml;
 */

/**
 * @Package org.apache.log4j
 * @ClassName: Level
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月9日 上午11:26:23
 * @Description:

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

    private static final String ALL_NAME = "ALL";

    private static final String TRACE_NAME = "TRACE";

    private static final String DEBUG_NAME = "DEBUG";

    private static final String INFO_NAME = "INFO";

    private static final String WARN_NAME = "WARN";

    private static final String ERROR_NAME = "ERROR";

    private static final String FATAL_NAME = "FATAL";

    private static final String OFF_NAME = "OFF";

    /**
     * TRACE level integer value.
     * 
     * @since 1.2.12
     */
    public static final int TRACE_INT = 5000;

    /**
     * The <code>OFF</code> has the highest possible rank and is intended to turn off logging.
     */
    final static public Level OFF = new Level(OFF_INT, OFF_NAME, 0);

    /**
     * The <code>FATAL</code> level designates very severe error events that will presumably lead the application to abort.
     */
    final static public Level FATAL = new Level(FATAL_INT, FATAL_NAME, 0);

    /**
     * The <code>ERROR</code> level designates error events that might still allow the application to continue running.
     */
    final static public Level ERROR = new Level(ERROR_INT, ERROR_NAME, 3);

    /**
     * The <code>WARN</code> level designates potentially harmful situations.
     */
    final static public Level WARN = new Level(WARN_INT, WARN_NAME, 4);

    /**
     * The <code>INFO</code> level designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    final static public Level INFO = new Level(INFO_INT, INFO_NAME, 6);

    /**
     * The <code>DEBUG</code> Level designates fine-grained informational events that are most useful to debug an application.
     */
    final static public Level DEBUG = new Level(DEBUG_INT, DEBUG_NAME, 7);

    /**
     * The <code>TRACE</code> Level designates finer-grained informational events than the <code>DEBUG</code level.
     * 
     * @since 1.2.12
     */
    public static final Level TRACE = new Level(TRACE_INT, TRACE_NAME, 7);

    /**
     * The <code>ALL</code> has the lowest possible rank and is intended to turn on all logging.
     */
    final static public Level ALL = new Level(ALL_INT, ALL_NAME, 7);

    /**
     * Serialization version id.
     */
    static final long serialVersionUID = 3491141966387921974L;

    /**
     * Instantiate a Level object.
     */
    protected Level(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    /**
     * Convert the string passed as argument to a level. If the conversion fails, then this method returns {@link #DEBUG}.
     */
    public static Level toLevel(String sArg) {
        return toLevel(sArg, Level.DEBUG);
    }

    /**
     * Convert an integer passed as argument to a level. If the conversion fails, then this method returns {@link #DEBUG}.
     */
    public static Level toLevel(int val) {
        return toLevel(val, Level.DEBUG);
    }

    /**
     * Convert an integer passed as argument to a level. If the conversion fails, then this method returns the specified default.
     */
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

    /**
     * Convert the string passed as argument to a level. If the conversion fails, then this method returns the value of
     * <code>defaultLevel</code>.
     */
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
        //
        // For Turkish i problem, see bug 40937
        //
        if (s.equals("\u0130NFO")) {
            return Level.INFO;
        }
        return defaultLevel;
    }

    /**
     * Custom deserialization of Level.
     * 
     * @param s
     *            serialization stream.
     * @throws IOException
     *             if IO exception.
     * @throws ClassNotFoundException
     *             if class not found.
     */
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        level = s.readInt();
        syslogEquivalent = s.readInt();
        levelStr = s.readUTF();
        if (levelStr == null) {
            levelStr = "";
        }
    }

    /**
     * Serialize level.
     * 
     * @param s
     *            serialization stream.
     * @throws IOException
     *             if exception during serialization.
     */
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(level);
        s.writeInt(syslogEquivalent);
        s.writeUTF(levelStr);
    }

    /**
     * Resolved deserialized level to one of the stock instances. May be overriden in classes derived from Level.
     * 
     * @return resolved object.
     * @throws ObjectStreamException
     *             if exception during resolution.
     */
    private Object readResolve() throws ObjectStreamException {
        //
        // if the deserizalized object is exactly an instance of Level
        //
        if (getClass() == Level.class) {
            return toLevel(level);
        }
        //
        // extension of Level can't substitute stock item
        //
        return this;
    }

}
