package com.liuyun.log4j.spi;

//输出位置选择接口
public interface RepositorySelector {

    //获取日志注册器
    LoggerRepository getLoggerRepository();

}

