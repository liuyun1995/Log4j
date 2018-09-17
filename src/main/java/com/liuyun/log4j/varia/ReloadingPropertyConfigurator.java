package com.liuyun.log4j.varia;

import java.io.InputStream;
import java.net.URL;
import com.liuyun.log4j.PropertyConfigurator;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.Configurator;

public class ReloadingPropertyConfigurator implements Configurator {

    PropertyConfigurator delegate = new PropertyConfigurator();

    public ReloadingPropertyConfigurator() {}

    public void doConfigure(InputStream inputStream, LoggerRepository repository) {}

    public void doConfigure(URL url, LoggerRepository repository) {}

}
