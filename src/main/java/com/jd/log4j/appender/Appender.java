package com.jd.log4j.appender;

import com.jd.log4j.Layout;
import com.jd.log4j.spi.LoggingEvent;
import com.jd.log4j.spi.Filter;
import com.jd.log4j.spi.ErrorHandler;

/**
 * Appender本身是对日志输出方式的一种抽象。
 * Appender负责定义日志输出的目的地，它可以是控制台（ConsoleAppender）、文件（FileAppender）、JMS服务器（JmsLogAppender）、以Email的形式发送出去（SMTPAppender）等。Appender是一个命名的实体，另外它还包含了对Layout、ErrorHandler、Filter等引用
 * <p>
 * Implement this interface for your own strategies for outputting log
 * statements.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public interface Appender {

    /**
     * 在过滤器列表结尾加一个过滤器
     *
     * @param newFilter 新的过滤器
     */
    void addFilter(Filter newFilter);

    /**
     * 返回第一个过滤器，因为每个过滤器都会包含关联的下一个过滤器，所以只需要返回第一个即可
     * Returns the head Filter. The Filters are organized in a linked list
     * and so all Filters on this Appender are available through the result.
     *
     * @return the head Filter or null, if no Filters are present
     * @since 1.1
     */
    Filter getFilter();

    /**
     * 清空过滤器
     */
    void clearFilters();

    /**
     * 关闭事件，某些具体的实现类，可能需要在关闭之前做一些操作，例如释放一些资源等等
     * Release any resources allocated within the appender such as file
     * handles, network connections, etc.
     * <p>
     * <p>It is a programming error to append to a closed appender.
     *
     * @since 0.8.4
     */
    void close();

    /**
     * 实际具体的打印日志操作
     * Log in <code>Appender</code> specific way. When appropriate,
     * Loggers will call the <code>doAppend</code> method of appender
     * implementations in order to log.
     * @param event     日志记录事件
     */
    void doAppend(LoggingEvent event);

    /**
     * 获取当前Appender的名称
     * @return  名称
     */
    String getName();

    /**
     * 为当前Appender设置名称
     * @param name  名称
     */
    void setName(String name);

    ErrorHandler getErrorHandler();

    void setErrorHandler(ErrorHandler errorHandler);

    /**
     * 获取日志格式化器
     * 每次打印日志，都需要按照指定的格式将日志输出出去，
     * @return  日志格式化器
     */
    Layout getLayout();

    /**
     * 为当前Appender设置一个日志格式化器
     * @param layout    日志格式化器
     */
    void setLayout(Layout layout);

    /**
     * 大致意思是：用来判断当前Appender是否需要一个Layout。
     * 如果返回false那么即使在配置文件中配置了，也会忽略掉。
     * 如果返回True那么会按照配置文件中配置的Layout去初始化。
     *
     * Configurators call this method to determine if the appender
     * requires a layout. If this method returns <code>true</code>,
     * meaning that layout is required, then the configurator will
     * configure an layout using the configuration information at its
     * disposal.  If this method returns <code>false</code>, meaning that
     * a layout is not required, then layout configuration will be
     * skipped even if there is available layout configuration
     * information at the disposal of the configurator..
     * <p>
     * <p>In the rather exceptional case, where the appender
     * implementation admits a layout but can also work without it, then
     * the appender should return <code>true</code>.
     *
     * @since 0.8.4
     */
    boolean requiresLayout();
}
