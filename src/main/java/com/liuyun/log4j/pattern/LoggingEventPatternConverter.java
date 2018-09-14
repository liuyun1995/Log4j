package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;


/**
 * LoggingEventPatternConverter is a base class for pattern converters
 * that can format information from instances of LoggingEvent.
 *
 * @author Curt Arnold
 */
public abstract class LoggingEventPatternConverter extends PatternConverter {
    /**
     * Constructs an instance of LoggingEventPatternConverter.
     *
     * @param name  name of converter.
     * @param style CSS style for output.
     */
    protected LoggingEventPatternConverter(
            final String name, final String style) {
        super(name, style);
    }

    /**
     * Formats an event into a string buffer.
     *
     * @param event      event to format, may not be null.
     * @param toAppendTo string buffer to which the formatted event will be appended.  May not be null.
     */
    public abstract void format(
            final LoggingEvent event, final StringBuffer toAppendTo);

    /**
     * {@inheritDoc}
     */
    public void format(final Object obj, final StringBuffer output) {
        if (obj instanceof LoggingEvent) {
            format((LoggingEvent) obj, output);
        }
    }

    /**
     * Normally pattern converters are not meant to handle Exceptions although
     * few pattern converters might.
     * <p>
     * By examining the return values for this method, the containing layout will
     * determine whether it handles throwables or not.
     *
     * @return true if this PatternConverter handles throwables
     */
    public boolean handlesThrowable() {
        return false;
    }
}
