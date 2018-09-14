package com.liuyun.log4j;

import com.liuyun.log4j.spi.LoggingEvent;

//简单格式化器
public class SimpleLayout extends Layout {

    StringBuffer sbuf = new StringBuffer(128);

    public SimpleLayout() {}

    public void activateOptions() {}

    //格式化方法
    public String format(LoggingEvent event) {

        sbuf.setLength(0);
        sbuf.append(event.getLevel().toString());
        sbuf.append(" - ");
        sbuf.append(event.getRenderedMessage());
        sbuf.append(LINE_SEP);
        return sbuf.toString();
    }

    //忽略异常
    public boolean ignoresThrowable() {
        return true;
    }

}
