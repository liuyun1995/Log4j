package com.liuyun.log4j.spi;

import com.liuyun.log4j.or.ObjectRenderer;
import com.liuyun.log4j.or.RendererMap;

public interface RendererSupport {

    public RendererMap getRendererMap();

    public void setRenderer(Class renderedClass, ObjectRenderer renderer);

}
