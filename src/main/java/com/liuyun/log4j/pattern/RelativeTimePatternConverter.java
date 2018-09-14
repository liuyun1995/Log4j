package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Return the relative time in milliseconds since loading of the LoggingEvent
 * class.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class RelativeTimePatternConverter extends LoggingEventPatternConverter {
    /**
     * Cached formatted timestamp.
     */
    private CachedTimestamp lastTimestamp = new CachedTimestamp(0, "");

    /**
     * Private constructor.
     */
    public RelativeTimePatternConverter() {
        super("Time", "time");
    }

    /**
     * Obtains an instance of RelativeTimePatternConverter.
     *
     * @param options options, currently ignored, may be null.
     * @return instance of RelativeTimePatternConverter.
     */
    public static RelativeTimePatternConverter newInstance(
            final String[] options) {
        return new RelativeTimePatternConverter();
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        long timestamp = event.timeStamp;

        if (!lastTimestamp.format(timestamp, toAppendTo)) {
            final String formatted =
                    Long.toString(timestamp - LoggingEvent.getStartTime());
            toAppendTo.append(formatted);
            lastTimestamp = new CachedTimestamp(timestamp, formatted);
        }
    }

    /**
     * Cached timestamp and formatted value.
     */
    private static final class CachedTimestamp {
        /**
         * Cached timestamp.
         */
        private final long timestamp;

        /**
         * Cached formatted timestamp.
         */
        private final String formatted;

        /**
         * Creates a new instance.
         *
         * @param timestamp timestamp.
         * @param formatted formatted timestamp.
         */
        public CachedTimestamp(long timestamp, final String formatted) {
            this.timestamp = timestamp;
            this.formatted = formatted;
        }

        /**
         * Appends the cached formatted timestamp to the buffer if timestamps match.
         *
         * @param newTimestamp requested timestamp.
         * @param toAppendTo   buffer to append formatted timestamp.
         * @return true if requested timestamp matched cached timestamp.
         */
        public boolean format(long newTimestamp, final StringBuffer toAppendTo) {
            if (newTimestamp == timestamp) {
                toAppendTo.append(formatted);

                return true;
            }

            return false;
        }
    }
}
