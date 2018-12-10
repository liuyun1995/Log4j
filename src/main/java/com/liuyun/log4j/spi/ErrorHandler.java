package com.liuyun.log4j.spi;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.Logger;

//错误处理接口
public interface ErrorHandler extends OptionHandler {

    //设置日志类
    void setLogger(Logger logger);

    //处理错误
    void error(String message, Exception e, int errorCode);

    //处理错误
    void error(String message);

    //处理错误
    void error(String message, Exception e, int errorCode, LoggingEvent event);

    //设置输出器
    void setAppender(Appender appender);

    //设置备份输出器
    void setBackupAppender(Appender appender);

}
