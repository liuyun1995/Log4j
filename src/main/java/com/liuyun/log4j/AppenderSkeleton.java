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

package com.liuyun.log4j;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.appender.net.SocketAppender;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.OptionHandler;
import com.liuyun.log4j.spi.Filter;
import com.liuyun.log4j.spi.ErrorHandler;
import com.liuyun.log4j.helpers.OnlyOnceErrorHandler;
import com.liuyun.log4j.helpers.LogLog;


/**
 *
 * 在Log4j中，基本上所有的具体的Appender都会继承当前类。所以当前类被命名为“Appender骨架”，
 * 也就是说，为所有具体的Appender提供一些通用的功能。
 * 比方说：
 * 1，日志等级阀门（应该是，比方说在配置文件中配置了threshold为WARN，那么只会打印大于等于WARN级别的日志）功能。
 * 2，对一般性的过滤器的支持（Appender接口中不是定义了添加Filter的功能么）。
 *
 * Abstract superclass of the other appenders in the package.
 * <p>
 * This class provides the code for common functionality功能, such as
 * support for threshold filtering and support for general filters.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.8.1
 */
public abstract class AppenderSkeleton implements Appender, OptionHandler {

	/**
     * 日志格式化器，如果具体实现类有自己特有的格式化器，那此处就不用设置了。
	 * The layout variable does not need to be set if the appender
	 * implementation has its own layout.
	 */
	protected Layout layout;

	/**
     * 当前Appender的名字
	 * Appenders are named.
	 */
	protected String name;

	/**
     * 日志阀门，如果没有filter的话，则使用阀门过滤
	 * There is no level threshold filtering by default.
	 */
	protected Priority threshold;

	/**
	 * It is assumed and enforced that errorHandler is never null.
	 */
	protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();

	/**
     * 第一个过滤器
	 * The first filter in the filter chain. Set to <code>null</code>
	 * initially.
	 */
	protected Filter headFilter;
	/**
     * 最后一个过滤器
	 * The last filter in the filter chain.
	 */
	protected Filter tailFilter;

	/**
     * 判断当前Appender是否已被关闭
	 * Is this appender closed?
	 */
	protected boolean closed = false;

	/**
     * 构造函数
	 * Create new instance.
	 */
	public AppenderSkeleton() {
		super();
	}

	/**
	 * Create new instance.
	 * Provided for compatibility with log4j 1.3.
	 *
	 * @param isActive true if appender is ready for use upon construction.
	 *                 Not used in log4j 1.2.x.
	 * @since 1.2.15
	 */
	protected AppenderSkeleton(final boolean isActive) {
		super();
	}


	/**
	 * Derived appenders should override this method if option structure
	 * requires it.
	 */
	@Override
	public void activateOptions() {
	}


	/**
	 * Add a filter to end of the filter list.
	 *
	 * @since 0.9.0
	 */
	@Override
	public void addFilter(Filter newFilter) {
		if (headFilter == null) {
			headFilter = tailFilter = newFilter;
		} else {
			tailFilter.setNext(newFilter);
			tailFilter = newFilter;
		}
	}

	/**
	 * Subclasses of <code>AppenderSkeleton</code> should implement this
	 * method to perform actual logging. See also {@link #doAppend
	 * AppenderSkeleton.doAppend} method.
	 *
	 * @since 0.9.0
	 */
	abstract protected void append(LoggingEvent event);


	/**
	 * Clear the filters chain.
	 *
	 * @since 0.9.0
	 */
	public void clearFilters() {
		headFilter = tailFilter = null;
	}

	/**
	 * Finalize this appender by calling the derived class'
	 * <code>close</code> method.
	 *
	 * @since 0.8.4
	 */
	public void finalize() {
		// An appender might be closed then garbage collected. There is no
		// point in closing twice.
		if (this.closed) {
			return;
		}

		LogLog.debug("Finalizing appender named [" + name + "].");
		close();
	}


	/**
	 * Return the currently set {@link ErrorHandler} for this
	 * Appender.
	 *
	 * @since 0.9.0
	 */
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	/**
	 * Set the {@link ErrorHandler} for this Appender.
	 *
	 * @since 0.9.0
	 */
	public
	synchronized void setErrorHandler(ErrorHandler eh) {
		if (eh == null) {
			// We do not throw exception here since the cause is probably a
			// bad config file.
			LogLog.warn("You have tried to set a null error-handler.");
		} else {
			this.errorHandler = eh;
		}
	}

	/**
	 * Returns the head Filter.
	 *
	 * @since 1.1
	 */
	public Filter getFilter() {
		return headFilter;
	}

	/**
	 * Return the first filter in the filter chain for this
	 * Appender. The return value may be <code>null</code> if no is
	 * filter is set.
	 */
	public final Filter getFirstFilter() {
		return headFilter;
	}

	/**
	 * Returns the layout of this appender. The value may be null.
	 */
	public Layout getLayout() {
		return layout;
	}

	/**
	 * Set the layout for this appender. Note that some appenders have
	 * their own (fixed) layouts or do not use one. For example, the
	 * {@link SocketAppender} ignores the layout set
	 * here.
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	/**
	 * Returns the name of this appender.
	 *
	 * @return name, may be null.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Set the name of this Appender.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns this appenders threshold level. See the {@link
	 * #setThreshold} method for the meaning of this option.
	 *
	 * @since 1.1
	 */
	public Priority getThreshold() {
		return threshold;
	}

	/**
	 * Set the threshold level. All log events with lower level
	 * than the threshold level are ignored by the appender.
	 * <p>
	 * <p>In configuration files this option is specified by setting the
	 * value of the <b>Threshold</b> option to a level
	 * string, such as "DEBUG", "INFO" and so on.
	 *
	 * @since 0.8.3
	 */
	public void setThreshold(Priority threshold) {
		this.threshold = threshold;
	}

	/**
	 * Check whether the message level is below the appender's
	 * threshold. If there is no threshold set, then the return value is
	 * always <code>true</code>.
	 */
	public boolean isAsSevereAsThreshold(Priority priority) {
		return ((threshold == null) || priority.isGreaterOrEqual(threshold));
	}

	/**
	 * This method performs threshold checks and invokes filters before
	 * delegating actual logging to the subclasses specific {@link
	 * AppenderSkeleton#append} method.
	 */
	@Override
	public synchronized void doAppend(LoggingEvent event) {
		if (closed) {
			LogLog.error("Attempted to append to closed appender named [" + name + "].");
			return;
		}

		if (!isAsSevereAsThreshold(event.getLevel())) {
			return;
		}

		Filter f = this.headFilter;

		FILTER_LOOP:
		while (f != null) {
			switch (f.decide(event)) {
				case Filter.DENY:
					return;
				case Filter.ACCEPT:
					break FILTER_LOOP;
				case Filter.NEUTRAL:
					f = f.getNext();
			}
		}

		this.append(event);
	}
}
