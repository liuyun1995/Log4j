package com.liuyun.log4j.spi;

import com.liuyun.log4j.or.ObjectRenderer;
import com.liuyun.log4j.or.RendererMap;

//渲染支持器接口
public interface RendererSupport {

    //获取渲染映射器
    public RendererMap getRendererMap();

    //设置渲染器
    public void setRenderer(Class renderedClass, ObjectRenderer renderer);

}
