package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LocationInfo;
import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Formats the class name of the site of the logging request.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class ClassNamePatternConverter extends NamePatternConverter {
    /**
     * Private constructor.
     *
     * @param options options, may be null.
     */
    private ClassNamePatternConverter(
            final String[] options) {
        super("Class Name", "class name", options);
    }

    /**
     * Gets an instance of ClassNamePatternConverter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static ClassNamePatternConverter newInstance(
            final String[] options) {
        return new ClassNamePatternConverter(options);
    }

    /**
     * Format a logging event.
     *
     * @param event      event to format.
     * @param toAppendTo string buffer to which class name will be appended.
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        final int initialLength = toAppendTo.length();
        LocationInfo li = event.getLocationInformation();

        if (li == null) {
            toAppendTo.append(LocationInfo.NA);
        } else {
            toAppendTo.append(li.getClassName());
        }

        abbreviate(initialLength, toAppendTo);
    }
}
