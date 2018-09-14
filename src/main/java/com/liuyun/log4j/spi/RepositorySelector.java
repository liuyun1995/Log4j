package com.liuyun.log4j.spi;

//输出位置选择接口
public interface RepositorySelector {

	LoggerRepository getLoggerRepository();

}

