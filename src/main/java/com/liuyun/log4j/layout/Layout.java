package com.liuyun.log4j.layout;

import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.OptionHandler;
import com.liuyun.log4j.xml.XMLLayout;

//格式化器接口
public abstract class Layout implements OptionHandler {

    // Note that the line.separator property can be looked up even by
    // applets.
    public final static String LINE_SEP = System.getProperty("line.separator");
    public final static int LINE_SEP_LEN = LINE_SEP.length();

    //获取文本类型
    public String getContentType() {
        return "text/plain";
    }

    //获取头部信息
    public String getHeader() {
        return null;
    }

    //获取脚部信息
    public String getFooter() {
        return null;
    }

    //抽象格式化方法
    abstract public String format(LoggingEvent event);

    //是否忽略异常
    abstract public boolean ignoresThrowable();

}
