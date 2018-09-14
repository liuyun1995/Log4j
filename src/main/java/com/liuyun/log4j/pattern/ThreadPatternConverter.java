package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Formats the event thread name.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class ThreadPatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final ThreadPatternConverter INSTANCE = new ThreadPatternConverter();

    /**
     * Private constructor.
     */
    private ThreadPatternConverter() {
        super("Thread", "thread");
    }

    /**
     * Obtains an instance of ThreadPatternConverter.
     *
     * @param options options, currently ignored, may be null.
     * @return instance of ThreadPatternConverter.
     */
    public static ThreadPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        toAppendTo.append(event.getThreadName());
    }
}
