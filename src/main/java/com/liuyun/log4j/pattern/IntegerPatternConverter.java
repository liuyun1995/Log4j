package com.liuyun.log4j.pattern;

import java.util.Date;


/**
 * Formats an integer.
 *
 * @author Curt Arnold
 */
public final class IntegerPatternConverter extends PatternConverter {
    /**
     * Singleton.
     */
    private static final IntegerPatternConverter INSTANCE =
            new IntegerPatternConverter();

    /**
     * Private constructor.
     */
    private IntegerPatternConverter() {
        super("Integer", "integer");
    }

    /**
     * Obtains an instance of pattern converter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static IntegerPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(Object obj, final StringBuffer toAppendTo) {
        if (obj instanceof Integer) {
            toAppendTo.append(obj.toString());
        }

        if (obj instanceof Date) {
            toAppendTo.append(Long.toString(((Date) obj).getTime()));
        }
    }
}
