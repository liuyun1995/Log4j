package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.LocationInfo;


/**
 * Return the event's line location information in a StringBuffer.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class FileLocationPatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final FileLocationPatternConverter INSTANCE = new FileLocationPatternConverter();

    /**
     * Private constructor.
     */
    private FileLocationPatternConverter() {
        super("File Location", "file");
    }

    /**
     * Obtains an instance of pattern converter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static FileLocationPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer output) {
        LocationInfo locationInfo = event.getLocationInformation();

        if (locationInfo != null) {
            output.append(locationInfo.getFileName());
        }
    }
}
