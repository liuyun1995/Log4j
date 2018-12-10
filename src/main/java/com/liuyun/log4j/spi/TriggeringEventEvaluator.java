package com.liuyun.log4j.spi;

//触发事件统计器
public interface TriggeringEventEvaluator {

    //是否触发事件
    public boolean isTriggeringEvent(LoggingEvent event);

}
