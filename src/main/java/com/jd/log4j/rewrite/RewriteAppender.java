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
package com.jd.log4j.rewrite;

import com.jd.log4j.helpers.AppenderAttachableImpl;
import com.jd.log4j.spi.AppenderAttachable;
import com.jd.log4j.spi.LoggingEvent;
import com.jd.log4j.xml.DOMConfigurator;
import com.jd.log4j.Appender;
import com.jd.log4j.AppenderSkeleton;
import com.jd.log4j.spi.OptionHandler;
import com.jd.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;

import java.util.Enumeration;
import java.util.Properties;

/**
 * @Package org.apache.log4j.rewrite
 * @ClassName: RewriteAppender
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月8日 下午8:32:37
 * @Description:这种附加了测井要求另一个appender后可能重写日志事件。
 * This appender forwards a logging request to another appender after possibly rewriting the logging event.
 * This appender (with the appropriate policy) replaces the MapFilter, PropertyFilter and ReflectionFilter
 */
public class RewriteAppender extends AppenderSkeleton  implements AppenderAttachable, UnrecognizedElementHandler {
   
	
	private RewritePolicy policy;
    private final AppenderAttachableImpl appenders;

    /**
     * Description:将AppenderAttachableImpl对象实例化。
     */
    public RewriteAppender() {
        appenders = new AppenderAttachableImpl();
    }

    protected void append(final LoggingEvent event) {
        LoggingEvent rewritten = event;
        if (policy != null) {
            rewritten = policy.rewrite(event);
        }
        if (rewritten != null) {
            synchronized (appenders) {
              appenders.appendLoopOnAppenders(rewritten);
            }
        }
    }

    public void addAppender(final Appender newAppender) {
      synchronized (appenders) {
        appenders.addAppender(newAppender);
      }
    }

    public Enumeration getAllAppenders() {
      synchronized (appenders) {
        return appenders.getAllAppenders();
      }
    }

    public Appender getAppender(final String name) {
      synchronized (appenders) {
        return appenders.getAppender(name);
      }
    }


    public void close() {
      closed = true;
      //
      //    close all attached appenders.
      //
      synchronized (appenders) {
        Enumeration iter = appenders.getAllAppenders();

        if (iter != null) {
          while (iter.hasMoreElements()) {
            Object next = iter.nextElement();

            if (next instanceof Appender) {
              ((Appender) next).close();
            }
          }
        }
      }
    }

    /**
     * Determines if specified appender is attached.
     * @param appender appender.
     * @return true if attached.
     */
    public boolean isAttached(final Appender appender) {
      synchronized (appenders) {
        return appenders.isAttached(appender);
      }
    }

    public boolean requiresLayout() {
      return false;
    }

    public void removeAllAppenders() {
      synchronized (appenders) {
        appenders.removeAllAppenders();
      }
    }

    public void removeAppender(final Appender appender) {
      synchronized (appenders) {
        appenders.removeAppender(appender);
      }
    }

    public void removeAppender(final String name) {
      synchronized (appenders) {
        appenders.removeAppender(name);
      }
    }


    public void setRewritePolicy(final RewritePolicy rewritePolicy) {
        policy = rewritePolicy;
    }


    public boolean parseUnrecognizedElement(final Element element,final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("rewritePolicy".equals(nodeName)) {
            Object rewritePolicy =
                    DOMConfigurator.parseElement(
                            element, props, RewritePolicy.class);
            if (rewritePolicy != null) {
                if (rewritePolicy instanceof OptionHandler) {
                    ((OptionHandler) rewritePolicy).activateOptions();
                }
                this.setRewritePolicy((RewritePolicy) rewritePolicy);
            }
            return true;
        }
        return false;
    }

}
