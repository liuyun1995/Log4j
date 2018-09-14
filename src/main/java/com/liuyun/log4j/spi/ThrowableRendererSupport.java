package com.liuyun.log4j.spi;

/**
 * Implemented by logger repositories that support configurable
 * rendering of Throwables.
 * @since 1.2.16
 */
public interface ThrowableRendererSupport {
    /**
     * Get throwable renderer.
     * @return throwable renderer, may be null.
     */
    ThrowableRenderer getThrowableRenderer();

    /**
     * Set throwable renderer.
     * @param renderer renderer, may be null.
     */
    void setThrowableRenderer(ThrowableRenderer renderer);

}
