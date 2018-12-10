package com.liuyun.log4j.rewrite;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.liuyun.log4j.Logger;
import com.liuyun.log4j.spi.LoggingEvent;

//属性重写策略
public class PropertyRewritePolicy implements RewritePolicy {

    private Map properties = Collections.EMPTY_MAP;

    //构造器
    public PropertyRewritePolicy() {}

    //设置属性
    public void setProperties(String props) {
        Map hashTable = new HashMap();
        StringTokenizer pairs = new StringTokenizer(props, ",");
        while (pairs.hasMoreTokens()) {
            StringTokenizer entry = new StringTokenizer(pairs.nextToken(), "=");
            hashTable.put(entry.nextElement().toString().trim(), entry.nextElement().toString().trim());
        }
        synchronized (this) {
            properties = hashTable;
        }
    }

    //重写方法
    public LoggingEvent rewrite(final LoggingEvent source) {
        if (!properties.isEmpty()) {
            Map rewriteProps = new HashMap(source.getProperties());
            for (Iterator iter = properties.entrySet().iterator();
                 iter.hasNext();
            ) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (!rewriteProps.containsKey(entry.getKey())) {
                    rewriteProps.put(entry.getKey(), entry.getValue());
                }
            }

            return new LoggingEvent(
                    source.getFQNOfLoggerClass(),
                    source.getLogger() != null ? source.getLogger() : Logger.getLogger(source.getLoggerName()),
                    source.getTimeStamp(),
                    source.getLevel(),
                    source.getMessage(),
                    source.getThreadName(),
                    source.getThrowableInformation(),
                    source.getNDC(),
                    source.getLocationInformation(),
                    rewriteProps);
        }
        return source;
    }

}
