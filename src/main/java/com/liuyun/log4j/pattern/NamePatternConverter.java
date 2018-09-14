package com.liuyun.log4j.pattern;


/**
 * Base class for other pattern converters which can return only parts of their name.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Curt Arnold
 */
public abstract class NamePatternConverter extends LoggingEventPatternConverter {
    /**
     * Abbreviator.
     */
    private final NameAbbreviator abbreviator;

    /**
     * Constructor.
     *
     * @param name    name of converter.
     * @param style   style name for associated output.
     * @param options options, may be null, first element will be interpreted as an abbreviation pattern.
     */
    protected NamePatternConverter(
            final String name, final String style, final String[] options) {
        super(name, style);

        if ((options != null) && (options.length > 0)) {
            abbreviator = NameAbbreviator.getAbbreviator(options[0]);
        } else {
            abbreviator = NameAbbreviator.getDefaultAbbreviator();
        }
    }

    /**
     * Abbreviate name in string buffer.
     *
     * @param nameStart starting position of name to abbreviate.
     * @param buf       string buffer containing name.
     */
    protected final void abbreviate(final int nameStart, final StringBuffer buf) {
        abbreviator.abbreviate(nameStart, buf);
    }
}
