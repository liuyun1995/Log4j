package com.liuyun.log4j.xml;

import com.liuyun.log4j.layout.Layout;
import com.liuyun.log4j.helpers.Transform;
import com.liuyun.log4j.appender.net.SMTPAppender;
import com.liuyun.log4j.spi.LocationInfo;
import com.liuyun.log4j.spi.LoggingEvent;
import java.util.Set;
import java.util.Arrays;

//xml格式化器
public class XMLLayout extends Layout {

    private final int DEFAULT_SIZE = 256;
    private final int UPPER_LIMIT = 2048;

    private StringBuffer buf = new StringBuffer(DEFAULT_SIZE);
    private boolean locationInfo = false;
    private boolean properties = false;

    //设置位置信息
    public void setLocationInfo(boolean flag) {
        locationInfo = flag;
    }

    //获取位置信息
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * Sets whether MDC key-value pairs should be output, default false.
     *
     * @param flag new value.
     * @since 1.2.15
     */
    public void setProperties(final boolean flag) {
        properties = flag;
    }

    /**
     * Gets whether MDC key-value pairs should be output.
     *
     * @return true if MDC key-value pairs are output.
     * @since 1.2.15
     */
    public boolean getProperties() {
        return properties;
    }

    /**
     * No options to activate.
     */
    public void activateOptions() {
    }


    /**
     * Formats a {@link LoggingEvent} in conformance with the log4j.dtd.
     */
    public String format(final LoggingEvent event) {

        // Reset working buffer. If the buffer is too large, then we need a new
        // one in order to avoid the penalty of creating a large array.
        if (buf.capacity() > UPPER_LIMIT) {
            buf = new StringBuffer(DEFAULT_SIZE);
        } else {
            buf.setLength(0);
        }

        // We yield to the \r\n heresy.

        buf.append("<log4j:event logger=\"");
        buf.append(Transform.escapeTags(event.getLoggerName()));
        buf.append("\" timestamp=\"");
        buf.append(event.timeStamp);
        buf.append("\" level=\"");
        buf.append(Transform.escapeTags(String.valueOf(event.getLevel())));
        buf.append("\" thread=\"");
        buf.append(Transform.escapeTags(event.getThreadName()));
        buf.append("\">\r\n");

        buf.append("<log4j:message><![CDATA[");
        // Append the rendered message. Also make sure to escape any
        // existing CDATA sections.
        Transform.appendEscapingCDATA(buf, event.getRenderedMessage());
        buf.append("]]></log4j:message>\r\n");

        String ndc = event.getNDC();
        if (ndc != null) {
            buf.append("<log4j:NDC><![CDATA[");
            Transform.appendEscapingCDATA(buf, ndc);
            buf.append("]]></log4j:NDC>\r\n");
        }

        String[] s = event.getThrowableStrRep();
        if (s != null) {
            buf.append("<log4j:throwable><![CDATA[");
            for (int i = 0; i < s.length; i++) {
                Transform.appendEscapingCDATA(buf, s[i]);
                buf.append("\r\n");
            }
            buf.append("]]></log4j:throwable>\r\n");
        }

        if (locationInfo) {
            LocationInfo locationInfo = event.getLocationInformation();
            buf.append("<log4j:locationInfo class=\"");
            buf.append(Transform.escapeTags(locationInfo.getClassName()));
            buf.append("\" method=\"");
            buf.append(Transform.escapeTags(locationInfo.getMethodName()));
            buf.append("\" file=\"");
            buf.append(Transform.escapeTags(locationInfo.getFileName()));
            buf.append("\" line=\"");
            buf.append(locationInfo.getLineNumber());
            buf.append("\"/>\r\n");
        }

        if (properties) {
            Set keySet = event.getPropertyKeySet();
            if (keySet.size() > 0) {
                buf.append("<log4j:properties>\r\n");
                Object[] keys = keySet.toArray();
                Arrays.sort(keys);
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i].toString();
                    Object val = event.getMDC(key);
                    if (val != null) {
                        buf.append("<log4j:data name=\"");
                        buf.append(Transform.escapeTags(key));
                        buf.append("\" value=\"");
                        buf.append(Transform.escapeTags(String.valueOf(val)));
                        buf.append("\"/>\r\n");
                    }
                }
                buf.append("</log4j:properties>\r\n");
            }
        }

        buf.append("</log4j:event>\r\n\r\n");

        return buf.toString();
    }

    /**
     * The XMLLayout prints and does not ignore exceptions. Hence the
     * return value <code>false</code>.
     */
    public boolean ignoresThrowable() {
        return false;
    }
}
