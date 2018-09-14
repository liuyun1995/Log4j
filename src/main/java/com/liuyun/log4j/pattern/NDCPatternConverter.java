package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Return the event's NDC in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class NDCPatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final NDCPatternConverter INSTANCE = new NDCPatternConverter();

    /**
     * Private constructor.
     */
    private NDCPatternConverter() {
        super("NDC", "ndc");
    }

    /**
     * Obtains an instance of NDCPatternConverter.
     *
     * @param options options, may be null.
     * @return instance of NDCPatternConverter.
     */
    public static NDCPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        toAppendTo.append(event.getNDC());
    }
}
