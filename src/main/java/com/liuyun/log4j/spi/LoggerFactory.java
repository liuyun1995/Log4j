package com.liuyun.log4j.spi;

import com.liuyun.log4j.Logger;

//日志工厂
public interface LoggerFactory {

	//获取新的日志类实例
	Logger makeNewLoggerInstance(String name);

}
