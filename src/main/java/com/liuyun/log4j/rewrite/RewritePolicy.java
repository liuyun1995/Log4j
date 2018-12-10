package com.liuyun.log4j.rewrite;

import com.liuyun.log4j.spi.LoggingEvent;

//重写策略
public interface RewritePolicy {

    //重写方法
    LoggingEvent rewrite(final LoggingEvent source);

}
