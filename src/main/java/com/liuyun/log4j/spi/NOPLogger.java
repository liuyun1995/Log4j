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

import com.liuyun.log4j.appender.Appender;
import com.liuyun.log4j.Level;
import com.liuyun.log4j.Logger;
import com.liuyun.log4j.Priority;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * No-operation implementation of Logger used by NOPLoggerRepository.
 * @since 1.2.15
 */
public final class NOPLogger extends Logger {
    /**
     * Create instance of Logger.
     * @param repo repository, may not be null.
     * @param name name, may not be null, use "root" for root logger.
     */
    public NOPLogger(NOPLoggerRepository repo, final String name) {
        super(name);
        this.repository = repo;
        this.level = Level.OFF;
        this.parent = this;
    }
    public void addAppender(final Appender newAppender) {
    }
    public void assertLog(final boolean assertion, final String msg) {
    }
    public void callAppenders(final LoggingEvent event) {
    }
    void closeNestedAppenders() {
    }
    public void debug(final Object message) {
    }
    public void debug(final Object message, final Throwable t) {
    }
    public void error(final Object message) {
    }
    public void error(final Object message, final Throwable t) {
    }
    public void fatal(final Object message) {
    }
    public void fatal(final Object message, final Throwable t) {
    }
    public Enumeration getAllAppenders() {
      return new Vector().elements();
    }
    public Appender getAppender(final String name) {
       return null;
    }
    public Level getEffectiveLevel() {
      return Level.OFF;
    }
    public Priority getChainedPriority() {
      return getEffectiveLevel();
    }
    public ResourceBundle getResourceBundle() {
      return null;
    }
    public void info(final Object message) {
    }

    public void info(final Object message, final Throwable t) {
    }
    public boolean isAttached(Appender appender) {
      return false;
    }
    public boolean isDebugEnabled() {
      return false;
    }

    public boolean isEnabledFor(final Priority level) {
      return false;
    }

    public boolean isInfoEnabled() {
      return false;
    }


    public void l7dlog(final Priority priority, final String key, final Throwable t) {
    }

    public void l7dlog(final Priority priority, final String key,  final Object[] params, final Throwable t) {
    }

    public void log(final Priority priority, final Object message, final Throwable t) {
    }

    public void log(final Priority priority, final Object message) {
    }

    public void log(final String callerFQCN, final Priority level, final Object message, final Throwable t) {
    }

    public void removeAllAppenders() {
    }
    public void removeAppender(Appender appender) {
    }
    public void removeAppender(final String name) {
    }
    public void setLevel(final Level level) {
    }
    public void setPriority(final Priority priority) {
    }

    public void setResourceBundle(final ResourceBundle bundle) {
    }
    public void warn(final Object message) {
    }
    public void warn(final Object message, final Throwable t) {
    }
    public void trace(Object message) {
    }
    public void trace(Object message, Throwable t) {
    }
    public boolean isTraceEnabled() {
        return false;
    }


}
