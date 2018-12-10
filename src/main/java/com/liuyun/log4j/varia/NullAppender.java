package com.liuyun.log4j.varia;

import com.liuyun.log4j.AppenderSkeleton;
import com.liuyun.log4j.spi.LoggingEvent;

//空的输出器
public class NullAppender extends AppenderSkeleton {

    private static NullAppender instance = new NullAppender();

    //构造器
    public NullAppender() {}

    //激活操作
    public void activateOptions() {}

    //获取实例
    public NullAppender getInstance() {
        return instance;
    }

    //获取实例
    public static NullAppender getNullAppender() {
        return instance;
    }

    //关闭操作
    public void close() {}

    //执行追加
    public void doAppend(LoggingEvent event) {
    }

    //追加日志事件
    protected void append(LoggingEvent event) {
    }

    //是否请求格式化
    public boolean requiresLayout() {
        return false;
    }

}
