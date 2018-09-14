package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Formats a string literal.
 *
 * @author Curt Arnold
 */
public final class LiteralPatternConverter extends LoggingEventPatternConverter {
    /**
     * String literal.
     */
    private final String literal;

    /**
     * Create a new instance.
     *
     * @param literal string literal.
     */
    public LiteralPatternConverter(final String literal) {
        super("Literal", "literal");
        this.literal = literal;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        toAppendTo.append(literal);
    }

    /**
     * {@inheritDoc}
     */
    public void format(final Object obj, final StringBuffer toAppendTo) {
        toAppendTo.append(literal);
    }
}
