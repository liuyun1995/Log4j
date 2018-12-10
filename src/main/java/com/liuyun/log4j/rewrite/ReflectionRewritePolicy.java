package com.liuyun.log4j.rewrite;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.Logger;
import com.liuyun.log4j.helpers.LogLog;

//反射重写策略
public class ReflectionRewritePolicy implements RewritePolicy {

    //重写方法
    public LoggingEvent rewrite(final LoggingEvent source) {
        Object msg = source.getMessage();
        if (!(msg instanceof String)) {
            Object newMsg = msg;
            Map rewriteProps = new HashMap(source.getProperties());

            try {
                PropertyDescriptor[] props = Introspector.getBeanInfo(msg.getClass(), Object.class).getPropertyDescriptors();
                if (props.length > 0) {
                    for (int i = 0; i < props.length; i++) {
                        try {
                            Object propertyValue = props[i].getReadMethod().invoke(msg, (Object[]) null);
                            if ("message".equalsIgnoreCase(props[i].getName())) {
                                newMsg = propertyValue;
                            } else {
                                rewriteProps.put(props[i].getName(), propertyValue);
                            }
                        } catch (Exception e) {
                            LogLog.warn("Unable to evaluate property " + props[i].getName(), e);
                        }
                    }
                    return new LoggingEvent(
                            source.getFQNOfLoggerClass(),
                            source.getLogger() != null ? source.getLogger() : Logger.getLogger(source.getLoggerName()),
                            source.getTimeStamp(),
                            source.getLevel(),
                            newMsg,
                            source.getThreadName(),
                            source.getThrowableInformation(),
                            source.getNDC(),
                            source.getLocationInformation(),
                            rewriteProps);
                }
            } catch (Exception e) {
                LogLog.warn("Unable to get property descriptors", e);
            }

        }
        return source;
    }

}
