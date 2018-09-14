package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;
import com.liuyun.log4j.spi.ThrowableInformation;


/**
 * Outputs the ThrowableInformation portion of the LoggingEvent.
 * By default, outputs the full stack trace.  %throwable{none}
 * or %throwable{0} suppresses the stack trace. %throwable{short}
 * or %throwable{1} outputs just the first line.  %throwable{n}
 * will output n lines for a positive integer or drop the last
 * -n lines for a negative integer.
 *
 * @author Paul Smith
 */
public class ThrowableInformationPatternConverter extends LoggingEventPatternConverter {

    /**
     * Maximum lines of stack trace to output.
     */
    private int maxLines = Integer.MAX_VALUE;

    /**
     * Private constructor.
     *
     * @param options options, may be null.
     */
    private ThrowableInformationPatternConverter(
            final String[] options) {
        super("Throwable", "throwable");

        if ((options != null) && (options.length > 0)) {
            if ("none".equals(options[0])) {
                maxLines = 0;
            } else if ("short".equals(options[0])) {
                maxLines = 1;
            } else {
                try {
                    maxLines = Integer.parseInt(options[0]);
                } catch (NumberFormatException ex) {
                }
            }
        }
    }

    /**
     * Gets an instance of the class.
     *
     * @param options pattern options, may be null.  If first element is "short",
     *                only the first line of the throwable will be formatted.
     * @return instance of class.
     */
    public static ThrowableInformationPatternConverter newInstance(
            final String[] options) {
        return new ThrowableInformationPatternConverter(options);
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        if (maxLines != 0) {
            ThrowableInformation information = event.getThrowableInformation();

            if (information != null) {
                String[] stringRep = information.getThrowableStrRep();

                int length = stringRep.length;
                if (maxLines < 0) {
                    length += maxLines;
                } else if (length > maxLines) {
                    length = maxLines;
                }

                for (int i = 0; i < length; i++) {
                    String string = stringRep[i];
                    toAppendTo.append(string).append("\n");
                }
            }
        }
    }

    /**
     * This converter obviously handles throwables.
     *
     * @return true.
     */
    public boolean handlesThrowable() {
        return true;
    }
}
