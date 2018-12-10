package com.liuyun.log4j.xml;

import org.w3c.dom.Element;
import java.util.Properties;

//未识别元素处理器
public interface UnrecognizedElementHandler {

    //解析未识别元素
    boolean parseUnrecognizedElement(Element element, Properties props) throws Exception;

}