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

package com.jd.log4j;

import com.jd.log4j.spi.LoggingEvent;
import com.jd.log4j.spi.OptionHandler;
import com.jd.log4j.xml.XMLLayout;

/**
 * @Package org.apache.log4j
 * @ClassName: Layout
 * @author 张明明  braveheart1115@163.com
 * @date 2016年5月8日 下午10:24:53
 * @Description:Layout负责将LoggingEvent中的信息格式化成一行日志信息。对不同格式的日志可能还需要提供头和尾等信息。
 * 另外有些Layout不会处理异常信息，此时ignoresThrowable()方法返回false，并且异常信息需要Appender来处理，如PatternLayout。
 */
public abstract class Layout implements OptionHandler {

  // Note that the line.separator property can be looked up even by
  // applets.
  public final static String LINE_SEP = System.getProperty("line.separator");
  public final static int LINE_SEP_LEN  = LINE_SEP.length();


  /**
     Implement this method to create your own layout format.
  */
  abstract public String format(LoggingEvent event);

  /**
     Returns the content type output by this layout. The base class
     returns "text/plain". 
  */
  public String getContentType() {
    return "text/plain";
  }

  /**
     Returns the header for the layout format. The base class returns
     <code>null</code>.  */
  public String getHeader() {
    return null;
  }

  /**
     Returns the footer for the layout format. The base class returns
     <code>null</code>.  */
  public String getFooter() {
    return null;
  }



  /**
     If the layout handles the throwable object contained within
     {@link LoggingEvent}, then the layout should return
     <code>false</code>. Otherwise, if the layout ignores throwable
     object, then the layout should return <code>true</code>.
     If ignoresThrowable is true, the appender is responsible for
     rendering the throwable.

     <p>The {@link SimpleLayout}, {@link TTCCLayout}, {@link
     PatternLayout} all return <code>true</code>. The {@link
  XMLLayout} returns <code>false</code>.

     @since 0.8.4 */
  abstract public boolean ignoresThrowable();

}
