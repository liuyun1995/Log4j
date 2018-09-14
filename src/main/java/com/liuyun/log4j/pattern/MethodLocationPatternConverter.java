package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LocationInfo;
import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Return the event's line location information in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class MethodLocationPatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final MethodLocationPatternConverter INSTANCE = new MethodLocationPatternConverter();

    /**
     * Private constructor.
     */
    private MethodLocationPatternConverter() {
        super("Method", "method");
    }

    /**
     * Obtains an instance of MethodLocationPatternConverter.
     *
     * @param options options, may be null.
     * @return instance of MethodLocationPatternConverter.
     */
    public static MethodLocationPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        LocationInfo locationInfo = event.getLocationInformation();

        if (locationInfo != null) {
            toAppendTo.append(locationInfo.getMethodName());
        }
    }
}
