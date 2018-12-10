package com.liuyun.log4j;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.appender.ConsoleAppender;
import com.liuyun.log4j.layout.PatternLayout;

//基础配置器
public class BasicConfigurator {

    //构造器
    protected BasicConfigurator() {}

    //配置方法
    public static void configure() {
        Logger root = Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
    }

    //配置方法
    public static void configure(Appender appender) {
        Logger root = Logger.getRootLogger();
        root.addAppender(appender);
    }

    //重置配置信息
    public static void resetConfiguration() {
        LogManager.resetConfiguration();
    }

}
