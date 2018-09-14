package com.liuyun.log4j.spi;

import java.io.InputStream;
import java.net.URL;

/**
 * Implemented by classes capable of configuring log4j using a URL.
 *
 * @author Anders Kristensen
 * @since 1.0
 */
public interface Configurator {

    /**
     Special level value signifying inherited behaviour. The current
     value of this string constant is <b>inherited</b>. {@link #NULL}
     is a synonym.  */
    /**
     * inherited:遗传的；继承权的；通过继承得到的
     */
    public static final String INHERITED = "inherited";

    /**
     * Special level signifying inherited behaviour, same as {@link
     * #INHERITED}. The current value of this string constant is
     * <b>null</b>.
     */
    public static final String NULL = "null";


    /**
     * 根据文件的输入流进行配置
     *
     * @param inputStream The InputStream to parse
     * @param repository  The hierarchy to operation upon.
     * @since 1.2.17
     */
    void doConfigure(InputStream inputStream, LoggerRepository repository);

    /**
     * 根据URL进行配置
     *
     * @param url        The URL to parse
     * @param repository The hierarchy to operation upon.
     */
    void doConfigure(URL url, LoggerRepository repository);
}
