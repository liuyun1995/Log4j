package com.liuyun.log4j.spi;

//过滤器
public abstract class Filter implements OptionHandler {

    //下一个过滤器
    public Filter next;

    /**
     * The log event must be dropped immediately without consulting
     * with the remaining filters, if any, in the chain.
     */
    public static final int DENY = -1;

    /**
     * This filter is neutral with respect to the log event. The
     * remaining filters, if any, should be consulted for a final decision.
     */
    public static final int NEUTRAL = 0;

    /**
     * The log event must be logged immediately without consulting with
     * the remaining filters, if any, in the chain.
     */
    public static final int ACCEPT = 1;


    /**
     * Usually filters options become active when set. We provide a
     * default do-nothing implementation for convenience.
     */
    public void activateOptions() {}


    /**
     * <p>If the decision is <code>DENY</code>, then the event will be
     * dropped. If the decision is <code>NEUTRAL</code>, then the next
     * filter, if any, will be invoked. If the decision is ACCEPT then
     * the event will be logged without consulting with other filters in
     * the chain.
     *
     * @param event The LoggingEvent to decide upon.
     * @return decision The decision of the filter.
     */
    public abstract int decide(LoggingEvent event);

    //设置下一个过滤器
    public void setNext(Filter next) {
        this.next = next;
    }

    //获取下一个过滤器
    public Filter getNext() {
        return next;
    }

}
