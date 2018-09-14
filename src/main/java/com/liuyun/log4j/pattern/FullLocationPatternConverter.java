package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LocationInfo;
import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Format the event's line location information.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class FullLocationPatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final FullLocationPatternConverter INSTANCE =
            new FullLocationPatternConverter();

    /**
     * Private constructor.
     */
    private FullLocationPatternConverter() {
        super("Full Location", "fullLocation");
    }

    /**
     * Obtains an instance of pattern converter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static FullLocationPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer output) {
        LocationInfo locationInfo = event.getLocationInformation();

        if (locationInfo != null) {
            output.append(locationInfo.fullInfo);
        }
    }
}
