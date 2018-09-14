package com.liuyun.log4j.spi;

//错误代码
public interface ErrorCode {

    public final int GENERIC_FAILURE = 0;
    public final int WRITE_FAILURE = 1;
    public final int FLUSH_FAILURE = 2;
    public final int CLOSE_FAILURE = 3;
    public final int FILE_OPEN_FAILURE = 4;
    public final int MISSING_LAYOUT = 5;
    public final int ADDRESS_PARSE_FAILURE = 6;

}
