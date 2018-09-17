package com.liuyun.log4j.spi;

//异常渲染器
public interface ThrowableRenderer {

    //进行异常渲染
    public String[] doRender(Throwable t);

}
