package com.liuyun.log4j.xml;

import org.w3c.dom.Element;
import java.util.Properties;

/**
 * When implemented by an object configured by DomConfigurator,
 * the handle method will be called when an unrecognized child
 * element is encountered.  Unrecognized child elements of
 * the log4j:configuration element will be dispatched to
 * the logger repository if it supports this interface.
 *
 * @since 1.2.15
 */
public interface UnrecognizedElementHandler {
    /**
     * Called to inform a configured object when
     * an unrecognized child element is encountered.
     * @param element element, may not be null.
     * @param props properties in force, may be null.
     * @return true if configured object recognized the element
     * @throws Exception throw an exception to prevent activation
     * of the configured object.
     */
    boolean parseUnrecognizedElement(Element element, Properties props) throws Exception;
}