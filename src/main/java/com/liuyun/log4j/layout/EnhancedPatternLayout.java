package com.liuyun.log4j.layout;

import com.liuyun.log4j.helpers.OptionConverter;
import com.liuyun.log4j.helpers.PatternConverter;
import com.liuyun.log4j.helpers.PatternParser;
import com.liuyun.log4j.pattern.BridgePatternConverter;
import com.liuyun.log4j.pattern.BridgePatternParser;
import com.liuyun.log4j.spi.LoggerRepository;
import com.liuyun.log4j.spi.LoggingEvent;

//增强模式格式器
public class EnhancedPatternLayout extends Layout {
    /**
     * Default pattern string for log output. Currently set to the
     * string <b>"%m%n"</b> which just prints the application supplied
     * message.
     */
    public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";

    /**
     * A conversion pattern equivalent to the TTCCCLayout.
     * Current value is <b>%r [%t] %p %c %x - %m%n</b>.
     */
    public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";

    /**
     * Initial size of internal buffer, no longer used.
     *
     * @deprecated since 1.3
     */
    protected final int BUF_SIZE = 256;

    /**
     * Maximum capacity of internal buffer, no longer used.
     *
     * @deprecated since 1.3
     */
    protected final int MAX_CAPACITY = 1024;

    /**
     * Customized pattern conversion rules are stored under this key in the
     * {@link LoggerRepository LoggerRepository} object store.
     */
    public static final String PATTERN_RULE_REGISTRY = "PATTERN_RULE_REGISTRY";


    /**
     * Initial converter for pattern.
     */
    private PatternConverter head;

    /**
     * Conversion pattern.
     */
    private String conversionPattern;

    /**
     * True if any element in pattern formats information from exceptions.
     */
    private boolean handlesExceptions;

    //构造器
    public EnhancedPatternLayout() {
        this(DEFAULT_CONVERSION_PATTERN);
    }

    //构造器
    public EnhancedPatternLayout(final String pattern) {
        this.conversionPattern = pattern;
        head = createPatternParser((pattern == null) ? DEFAULT_CONVERSION_PATTERN : pattern).parse();
        if (head instanceof BridgePatternConverter) {
            handlesExceptions = !((BridgePatternConverter) head).ignoresThrowable();
        } else {
            handlesExceptions = false;
        }
    }

    //设置转换模式
    public void setConversionPattern(final String conversionPattern) {
        this.conversionPattern =
                OptionConverter.convertSpecialChars(conversionPattern);
        head = createPatternParser(this.conversionPattern).parse();
        if (head instanceof BridgePatternConverter) {
            handlesExceptions = !((BridgePatternConverter) head).ignoresThrowable();
        } else {
            handlesExceptions = false;
        }
    }

    //获取转换模式
    public String getConversionPattern() {
        return conversionPattern;
    }

    //创建模式解析器
    protected PatternParser createPatternParser(String pattern) {
        return new BridgePatternParser(pattern);
    }

    //激活操作
    public void activateOptions() {}

    //格式化方法
    public String format(final LoggingEvent event) {
        StringBuffer buf = new StringBuffer();
        for (PatternConverter c = head; c != null; c = c.next) {
            c.format(buf, event);
        }
        return buf.toString();
    }

    //是否忽略异常
    public boolean ignoresThrowable() {
        return !handlesExceptions;
    }

}
