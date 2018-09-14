package com.liuyun.log4j.spi;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.Logger;

//错误处理接口
public interface ErrorHandler extends OptionHandler {

    //设置日志类
    void setLogger(Logger logger);


    /**
     * Equivalent to the {@link #error(String, Exception, int,
     * LoggingEvent event)} with the the event parameteter set to
     * <code>null</code>.
     */
    void error(String message, Exception e, int errorCode);

    /**
     * This method is normally used to just print the error message
     * passed as a parameter.
     */
    void error(String message);

    /**
     * This method is invoked to handle the error.
     *
     * @param message   The message assoicated with the error.
     * @param e         The Exption that was thrown when the error occured.
     * @param errorCode The error code associated with the error.
     * @param event     The logging event that the failing appender is asked
     *                  to log.
     * @since 1.2
     */
    void error(String message, Exception e, int errorCode, LoggingEvent event);

    /**
     * Set the appender for which errors are handled. This method is
     * usually called when the error handler is configured.
     *
     * @since 1.2
     */
    void setAppender(Appender appender);

    /**
     * Set the appender to falkback upon in case of failure.
     *
     * @since 1.2
     */
    void setBackupAppender(Appender appender);
}
