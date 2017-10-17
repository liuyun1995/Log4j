/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jd.log4j.spi;

import com.jd.log4j.Appender;

import java.util.Enumeration;

/**
 * @author 张明明  braveheart1115@163.com
 * @Package org.apache.log4j.spi
 * @ClassName: AppenderAttachable
 * @date 2016年5月8日 下午8:23:10
 * @Description:将附加的内容输出到对象上。 Interface for attaching(附加) appenders(输出) to objects.
 */
public interface AppenderAttachable {


	/**
	 * 添加输出源
	 *
	 * @param newAppender
	 */
	public void addAppender(Appender newAppender);

	/**
	 * 获取所有的输出源
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Enumeration getAllAppenders();

	/**
	 * 根据名称获取输出源
	 *
	 * @param name
	 * @return
	 */
	public Appender getAppender(String name);

	/**
	 * @param appender
	 * @author 张明明
	 * @date 2016年5月8日 下午8:26:42
	 * @Description: 是否是可以附加的。
	 */
	public boolean isAttached(Appender appender);

	/**
	 * 删除所有的输出源
	 */
	void removeAllAppenders();


	/**
	 * 按照输出源删除输出源
	 *
	 * @param appender
	 */
	void removeAppender(Appender appender);

	/**
	 * 按照名字删除输出源
	 *
	 * @param name
	 */
	void removeAppender(String name);
}

