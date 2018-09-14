package com.liuyun.log4j.spi;

import com.liuyun.log4j.Category;
import com.liuyun.log4j.DefaultThrowableRenderer;

//异常信息
public class ThrowableInformation implements java.io.Serializable {

    static final long serialVersionUID = -4748765566864322735L;

    private transient Throwable throwable;
    private transient Category category;
    private String[] rep;

    public ThrowableInformation(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Create a new instance.
     *
     * @param throwable throwable, may not be null.
     * @param category  category used to obtain ThrowableRenderer, may be null.
     * @since 1.2.16
     */
    public ThrowableInformation(Throwable throwable, Category category) {
        this.throwable = throwable;
        this.category = category;
    }

    /**
     * Create new instance.
     *
     * @param r String representation of throwable.
     * @since 1.2.15
     */
    public ThrowableInformation(final String[] r) {
        if (r != null) {
            rep = (String[]) r.clone();
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public synchronized String[] getThrowableStrRep() {
        if (rep == null) {
            ThrowableRenderer renderer = null;
            if (category != null) {
                LoggerRepository repo = category.getLoggerRepository();
                if (repo instanceof ThrowableRendererSupport) {
                    renderer = ((ThrowableRendererSupport) repo).getThrowableRenderer();
                }
            }
            if (renderer == null) {
                rep = DefaultThrowableRenderer.render(throwable);
            } else {
                rep = renderer.doRender(throwable);
            }
        }
        return (String[]) rep.clone();
    }
}


