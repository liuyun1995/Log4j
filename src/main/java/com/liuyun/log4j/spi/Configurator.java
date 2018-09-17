package com.liuyun.log4j.spi;

import java.io.InputStream;
import java.net.URL;

//配置器接口
public interface Configurator {

    public static final String INHERITED = "inherited";
    public static final String NULL = "null";

    //根据输入流进行配置
    void doConfigure(InputStream inputStream, LoggerRepository repository);

    //根据URL进行配置
    void doConfigure(URL url, LoggerRepository repository);

}
