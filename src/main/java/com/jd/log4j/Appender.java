package com.jd.log4j;

import com.jd.log4j.spi.LoggingEvent;
import com.jd.log4j.spi.Filter;
import com.jd.log4j.spi.ErrorHandler;

/**
 * @author 张明明  braveheart1115@163.com
 * @Package org.apache.log4j
 * @ClassName: Appender
 * @date 2016年5月8日 下午7:45:47
 * @Description:实现此接口，用于输出日志的策略。指定了日志将打印到控制台还是文件中。 日志目的地，把格式化好的日志信息输出到指定的地方去。
 * Implement this interface for your own strategies(策略) for outputting log  statements.
 */
public interface Appender {

	/**
	 * 在过滤器列表结尾加一个过滤器
	 * @param newFilter	新的过滤器
	 */
	void addFilter(Filter newFilter);

	/**
	 Returns the head Filter. The Filters are organized in a linked list
	 and so all Filters on this Appender are available through the result.

	 @return the head Filter or null, if no Filters are present
	 @since 1.1
	 */
	/**
	 * @author 张明明
	 * @date 2016年5月8日 下午7:50:33
	 * @Description:返回头过滤器。过滤器的组织是一个链表，所以所有的滤波器都可以通过这个附加的结果。如果没有过滤器则返回空。 Returns the head Filter. The Filters are organized in a linked list
	 * and so all Filters on this Appender are available through the result.
	 */
	Filter getFilter();

	void clearFilters();

	/**
	 * Release any resources allocated within the appender such as file
	 * handles, network connections, etc.
	 * <p>
	 * <p>It is a programming error to append to a closed appender.
	 *
	 * @since 0.8.4
	 */
	void close();

	/**
	 Log in <code>Appender</code> specific way. When appropriate,
	 Loggers will call the <code>doAppend</code> method of appender
	 implementations in order to log. */

	/**
	 * @param event
	 * @author 张明明
	 * @date 2016年5月8日 下午7:45:26
	 * @Description:
	 */
	void doAppend(LoggingEvent event);

	String getName();

	void setName(String name);

	ErrorHandler getErrorHandler();

	void setErrorHandler(ErrorHandler errorHandler);

	Layout getLayout();

	void setLayout(Layout layout);

	/**
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
