package com.liuyun.log4j.varia;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.Logger;
import com.liuyun.log4j.spi.ErrorHandler;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.helpers.LogLog;
import java.util.Vector;
import java.io.InterruptedIOException;

//回调错误处理器
public class FallbackErrorHandler implements ErrorHandler {

    Appender backup;
    Appender primary;
    Vector loggers;

    //构造器
    public FallbackErrorHandler() {}

    //设置日志类
    public void setLogger(Logger logger) {
        LogLog.debug("FB: Adding logger [" + logger.getName() + "].");
        if (loggers == null) {
            loggers = new Vector();
        }
        loggers.addElement(logger);
    }

    //激活操作
    public void activateOptions() {}


    //处理错误
    public void error(String message, Exception e, int errorCode) {
        error(message, e, errorCode, null);
    }

    //处理错误
    public void error(String message, Exception e, int errorCode, LoggingEvent event) {
        if (e instanceof InterruptedIOException) {
            Thread.currentThread().interrupt();
        }
        LogLog.debug("FB: The following error reported: " + message, e);
        LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
        if (loggers != null) {
            for (int i = 0; i < loggers.size(); i++) {
                Logger l = (Logger) loggers.elementAt(i);
                LogLog.debug("FB: Searching for [" + primary.getName() + "] in logger ["
                        + l.getName() + "].");
                LogLog.debug("FB: Replacing [" + primary.getName() + "] by ["
                        + backup.getName() + "] in logger [" + l.getName() + "].");
                l.removeAppender(primary);
                LogLog.debug("FB: Adding appender [" + backup.getName() + "] to logger "
                        + l.getName());
                l.addAppender(backup);
            }
        }
    }

    //处理错误
    public void error(String message) {}

    //设置输出器
    public void setAppender(Appender primary) {
        LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
        this.primary = primary;
    }

    //设置备份输出器
    public void setBackupAppender(Appender backup) {
        LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
        this.backup = backup;
    }

}
