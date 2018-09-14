package com.liuyun.log4j.spi;

public interface TriggeringEventEvaluator {

    public boolean isTriggeringEvent(LoggingEvent event);

}
