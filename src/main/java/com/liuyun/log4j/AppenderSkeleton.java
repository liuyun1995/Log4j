package com.liuyun.log4j;

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.appender.net.SocketAppender;
import com.liuyun.log4j.layout.Layout;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.OptionHandler;
import com.liuyun.log4j.spi.Filter;
import com.liuyun.log4j.spi.ErrorHandler;
import com.liuyun.log4j.helpers.OnlyOnceErrorHandler;
import com.liuyun.log4j.helpers.LogLog;

//输出器骨架类
public abstract class AppenderSkeleton implements Appender, OptionHandler {

	//日志格式化器
	protected Layout layout;
	//当前输出器名称
	protected String name;
	//日志阀门
	protected Priority threshold;
	//错误处理器
	protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();
	//头部过滤器
	protected Filter headFilter;
	//末尾过滤器
	protected Filter tailFilter;
	//当前输出器是否已被关闭
	protected boolean closed = false;

	public AppenderSkeleton() {
		super();
	}

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


	//添加过滤器
	@Override
	public void addFilter(Filter newFilter) {
		if (headFilter == null) {
			headFilter = tailFilter = newFilter;
		} else {
			tailFilter.setNext(newFilter);
			tailFilter = newFilter;
		}
	}

	//输出日志信息
	abstract protected void append(LoggingEvent event);

	//清空过滤器
	public void clearFilters() {
		headFilter = tailFilter = null;
	}

	//销毁方法
	public void finalize() {
		if (this.closed) {
			return;
		}
		LogLog.debug("Finalizing appender named [" + name + "].");
		close();
	}

	//获取错误处理器
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	//设置错误处理器
	synchronized public void setErrorHandler(ErrorHandler eh) {
		if (eh == null) {
			LogLog.warn("You have tried to set a null error-handler.");
		} else {
			this.errorHandler = eh;
		}
	}

	//获取头部过滤器
	public Filter getFilter() {
		return headFilter;
	}

	//获取头部过滤器
	public final Filter getFirstFilter() {
		return headFilter;
	}

	//获取格式化器
	public Layout getLayout() {
		return layout;
	}

	//设置格式化器
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	//获取输出器名称
	public final String getName() {
		return this.name;
	}

	//设置输出器名称
	public void setName(String name) {
		this.name = name;
	}

	//获取日志阀值
	public Priority getThreshold() {
		return threshold;
	}

	//设置日志阀值
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

	//输出日志信息
	@Override
	public synchronized void doAppend(LoggingEvent event) {
		if (closed) {
			LogLog.error("Attempted to append to closed appender named [" + name + "].");
			return;
		}

		if (!isAsSevereAsThreshold(event.getLevel())) {
			return;
		}

		//获取首个过滤器
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
		//调用具体子类的方法输出日志
		this.append(event);
	}
}
