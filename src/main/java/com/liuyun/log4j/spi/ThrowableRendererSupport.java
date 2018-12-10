package com.liuyun.log4j.spi;

//异常渲染器支持
public interface ThrowableRendererSupport {

    //获取异常渲染器
    ThrowableRenderer getThrowableRenderer();

    //设置异常渲染器
    void setThrowableRenderer(ThrowableRenderer renderer);

}
