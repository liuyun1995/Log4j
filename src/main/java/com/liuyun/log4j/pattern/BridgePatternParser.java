package com.liuyun.log4j.pattern;

import com.liuyun.log4j.helpers.PatternConverter;
import com.liuyun.log4j.helpers.PatternParser;

/**
 * The class implements the pre log4j 1.3 PatternConverter
 * contract by delegating to the log4j 1.3 pattern implementation.
 *
 * @author Curt Arnold
 */
public final class BridgePatternParser extends PatternParser {


    /**
     * Create a new instance.
     *
     * @param conversionPattern pattern, may not be null.
     */
    public BridgePatternParser(
            final String conversionPattern) {
        super(conversionPattern);
    }

    /**
     * Create new pattern converter.
     *
     * @return pattern converter.
     */
    public PatternConverter parse() {
        return new BridgePatternConverter(pattern);
    }
}
