package com.liuyun.log4j.spi;

/**
 * Implemented by classes that render instances of
 * java.lang.Throwable (exceptions and errors)
 * into a string representation.
 *
 * @since 1.2.16
 */
public interface ThrowableRenderer {
    /**
     * Render Throwable.
     *
     * @param t throwable, may not be null.
     * @return String representation.
     */
    public String[] doRender(Throwable t);

}
