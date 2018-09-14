package com.liuyun.log4j;

import com.liuyun.log4j.spi.LoggerFactory;

public class DefaultCategoryFactory implements LoggerFactory {

	DefaultCategoryFactory() {
	}

	@Override
	public Logger makeNewLoggerInstance(String name) {
		return new Logger(name);
	}

}
