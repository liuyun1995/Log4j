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

package com.jd.log4j.varia;

import java.io.InputStream;
import java.net.URL;

import com.jd.log4j.PropertyConfigurator;
import com.jd.log4j.spi.LoggerRepository;
import com.jd.log4j.spi.Configurator;

public class ReloadingPropertyConfigurator implements Configurator {

    PropertyConfigurator delegate = new PropertyConfigurator();

    public ReloadingPropertyConfigurator() {
    }

   /**
    * @since 1.2.17
    */
    public void doConfigure(InputStream inputStream, LoggerRepository repository) {
    }

    public void doConfigure(URL url, LoggerRepository repository) {
    }

}