package com.liuyun.log4j.varia;

import java.io.InputStream;
import java.net.URL;
import com.liuyun.log4j.PropertyConfigurator;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.Configurator;

//重新加载属性配置器
public class ReloadingPropertyConfigurator implements Configurator {

    PropertyConfigurator delegate = new PropertyConfigurator();

    //构造器
    public ReloadingPropertyConfigurator() {}

    //配置方法
    public void doConfigure(InputStream inputStream, LoggerRepository repository) {}

    //配置方法
    public void doConfigure(URL url, LoggerRepository repository) {}

}
