package com.liuyun.log4j.pattern;

/**
 * Formats an date by delegating to DatePatternConverter.  The default
 * date pattern for a %d specifier in a file name is different than
 * the %d pattern in pattern layout.
 *
 * @author Curt Arnold
 */
public final class FileDatePatternConverter {
    /**
     * Private constructor.
     */
    private FileDatePatternConverter() {}

    /**
     * Obtains an instance of pattern converter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static PatternConverter newInstance(
            final String[] options) {
        if ((options == null) || (options.length == 0)) {
            return DatePatternConverter.newInstance(
                    new String[]{
                            "yyyy-MM-dd"
                    });
        }

        return DatePatternConverter.newInstance(options);
    }
}
