package com.liuyun.log4j;

import com.liuyun.log4j.spi.LoggerFactory;

//默认日志类型工厂
public class DefaultCategoryFactory implements LoggerFactory {

	DefaultCategoryFactory() {}

	//获取新日志实例
	@Override
	public Logger makeNewLoggerInstance(String name) {
		return new Logger(name);
	}

}
