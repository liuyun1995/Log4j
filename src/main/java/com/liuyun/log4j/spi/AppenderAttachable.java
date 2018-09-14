package com.liuyun.log4j.spi;

import com.liuyun.log4j.appender.Appender;
import java.util.Enumeration;

public interface AppenderAttachable {

	//添加输出源
	void addAppender(Appender newAppender);

	//获取所有的输出源
	@SuppressWarnings("rawtypes")
	Enumeration getAllAppenders();

	//根据名称获取输出源
	Appender getAppender(String name);

	//是否是可以附加的
	boolean isAttached(Appender appender);

	//删除所有的输出源
	void removeAllAppenders();

	//按照输出源删除输出源
	void removeAppender(Appender appender);

	//按照名字删除输出源
	void removeAppender(String name);

}

