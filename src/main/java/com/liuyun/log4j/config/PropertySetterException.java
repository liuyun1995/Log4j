package com.liuyun.log4j.config;

//属性设置异常
public class PropertySetterException extends Exception {
    private static final long serialVersionUID = -1352613734254235861L;
    protected Throwable rootCause;

    public PropertySetterException(String msg) {
        super(msg);
    }

    public PropertySetterException(Throwable rootCause) {
        super();
        this.rootCause = rootCause;
    }

    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null && rootCause != null) {
            msg = rootCause.getMessage();
        }
        return msg;
    }
}
