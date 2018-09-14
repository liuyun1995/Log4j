package com.liuyun.log4j.appender;

import com.liuyun.log4j.Layout;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.Filter;
import com.liuyun.log4j.spi.ErrorHandler;

//日志输出器
public interface Appender {

    //添加过滤器
    void addFilter(Filter newFilter);

    //获取第一个过滤器
    Filter getFilter();

    //清空过滤器
    void clearFilters();

    //关闭输出器
    void close();

    //打印日志操作
    void doAppend(LoggingEvent event);

    //获取输出器名称
    String getName();

    //设置输出器名称
    void setName(String name);

    //获取错误处理器
    ErrorHandler getErrorHandler();

    //设置错误处理器
    void setErrorHandler(ErrorHandler errorHandler);

    //获取日志格式化器
    Layout getLayout();

    //设置日志格式化器
    void setLayout(Layout layout);

    //是否需要日志格式化器
    boolean requiresLayout();

}
