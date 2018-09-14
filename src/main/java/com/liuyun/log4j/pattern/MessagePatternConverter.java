package com.liuyun.log4j.pattern;

import com.liuyun.log4j.spi.LoggingEvent;

//消息模式转换器
public final class MessagePatternConverter extends LoggingEventPatternConverter {
    /**
     * Singleton.
     */
    private static final MessagePatternConverter INSTANCE = new MessagePatternConverter();

    /**
     * Private constructor.
     */
    private MessagePatternConverter() {
        super("Message", "message");
    }

    /**
     * Obtains an instance of pattern converter.
     *
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static MessagePatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void format(final LoggingEvent event, final StringBuffer toAppendTo) {
        toAppendTo.append(event.getRenderedMessage());
    }
}
