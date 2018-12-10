package com.liuyun.log4j.or;

import com.liuyun.log4j.helpers.OptionConverter;
import com.liuyun.log4j.spi.RendererSupport;
import com.liuyun.log4j.helpers.LogLog;
import com.liuyun.log4j.helpers.Loader;

import java.util.Hashtable;

//渲染器映射
public class RendererMap {

    Hashtable map;

    static ObjectRenderer defaultRenderer = new DefaultRenderer();

    //构造器
    public RendererMap() {
        map = new Hashtable();
    }

    //添加渲染器
    public static void addRenderer(RendererSupport repository, String renderedClassName, String renderingClassName) {
        LogLog.debug("Rendering class: [" + renderingClassName + "], Rendered class: [" + renderedClassName + "].");
        ObjectRenderer renderer = (ObjectRenderer) OptionConverter.instantiateByClassName(renderingClassName, ObjectRenderer.class, null);
        if (renderer == null) {
            LogLog.error("Could not instantiate renderer [" + renderingClassName + "].");
            return;
        } else {
            try {
                Class renderedClass = Loader.loadClass(renderedClassName);
                repository.setRenderer(renderedClass, renderer);
            } catch (ClassNotFoundException e) {
                LogLog.error("Could not find class [" + renderedClassName + "].", e);
            }
        }
    }


    //寻找渲染器渲染对象
    public String findAndRender(Object o) {
        if (o == null) {
            return null;
        } else {
            return get(o.getClass()).doRender(o);
        }
    }

    //获取渲染器
    public ObjectRenderer get(Object o) {
        if (o == null) {
            return null;
        } else {
            return get(o.getClass());
        }
    }

    //获取渲染器
    public ObjectRenderer get(Class clazz) {
        ObjectRenderer r = null;
        for (Class c = clazz; c != null; c = c.getSuperclass()) {
            r = (ObjectRenderer) map.get(c);
            if (r != null) {
                return r;
            }
            r = searchInterfaces(c);
            if (r != null) {
                return r;
            }
        }
        return defaultRenderer;
    }

    //寻找渲染器接口
    ObjectRenderer searchInterfaces(Class c) {
        ObjectRenderer r = (ObjectRenderer) map.get(c);
        if (r != null) {
            return r;
        } else {
            Class[] ia = c.getInterfaces();
            for (int i = 0; i < ia.length; i++) {
                r = searchInterfaces(ia[i]);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    //获取默认渲染器
    public ObjectRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    //清空渲染器
    public void clear() {
        map.clear();
    }

    //放置渲染器映射
    public void put(Class clazz, ObjectRenderer or) {
        map.put(clazz, or);
    }

}
