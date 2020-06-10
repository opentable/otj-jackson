/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentable.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        OpenTableJacksonModuleRegistrationTest.Config.class
})
public class OpenTableJacksonModuleRegistrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    @Import(OpenTableJacksonConfiguration.class)
    public static class Config {

        @Bean
        public Module testModule() {
            return new SimpleModule("test-module") {
                @Override
                public Object getTypeId() {
                    return "test-module-id";
                }
            };
        }

        @Bean
        public Module testModule2() {
            return new SimpleModule("test-module2") {
                @Override
                public Object getTypeId() {
                    return "test-module2-id";
                }
            };
        }
    }

    @Test
    public void testModulesRegistered() {
        Assert.assertEquals(1, objectMapper.getRegisteredModuleIds().stream().filter(m -> m.equals("test-module-id")).count());
        Assert.assertEquals(1, objectMapper.getRegisteredModuleIds().stream().filter(m -> m.equals("test-module2-id")).count());
    }

}
