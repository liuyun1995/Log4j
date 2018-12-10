package com.liuyun.log4j.rewrite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.Logger;

//映射重写策略
public class MapRewritePolicy implements RewritePolicy {

    //重写方法
    public LoggingEvent rewrite(final LoggingEvent source) {
        Object msg = source.getMessage();
        if (msg instanceof Map) {
            Map props = new HashMap(source.getProperties());
            Map eventProps = (Map) msg;
            Object newMsg = eventProps.get("message");
            if (newMsg == null) {
                newMsg = msg;
            }

            for (Iterator iter = eventProps.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (!("message".equals(entry.getKey()))) {
                    props.put(entry.getKey(), entry.getValue());
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
                    props);
        } else {
            return source;
        }

    }

}
