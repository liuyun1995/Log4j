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


package com.liuyun.log4j.spi;


/**
 * @author 张明明  braveheart1115@163.com
 * @Package org.apache.log4j.spi
 * @ClassName: DefaultRepositorySelector
 * @date 2016年5月7日 下午5:21:51
 * @Description: 默认仓库选择类。
 */
public class DefaultRepositorySelector implements RepositorySelector {

	final LoggerRepository repository;

	/**
	 * @param repository Description:构造方法。
	 */
	public DefaultRepositorySelector(LoggerRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns a {@link LoggerRepository} depending on the
	 * context. Implementors must make sure that a valid (non-null)
	 * LoggerRepository is returned.
	 */
	@Override
	public LoggerRepository getLoggerRepository() {
		return repository;
	}
}

