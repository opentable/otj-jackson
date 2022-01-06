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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class TestCustomizer {

    @Autowired
    private ObjectMapper objectMapper;


    @Configuration
    @Import(OpenTableJacksonConfiguration.class)
    public static class TestApplication {

        @Bean
        public OpenTableJacksonCustomizer customizer() {
            return objectMapper -> objectMapper.addMixIn(SimpleObject.class, MyMixin.class);
        }

        @JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
        interface MyMixin {}
    }

    public static class SimpleObject {

        public SimpleObject(String keyWithLongNameThatNeedsHyphens, String valueWithLongNameThatNeedsHyphens) {
            this.keyWithLongNameThatNeedsHyphens = keyWithLongNameThatNeedsHyphens;
            this.valueWithLongNameThatNeedsHyphens = valueWithLongNameThatNeedsHyphens;
        }
        private final String keyWithLongNameThatNeedsHyphens;
        private final String valueWithLongNameThatNeedsHyphens;

        public String getKeyWithLongNameThatNeedsHyphens() {
            return keyWithLongNameThatNeedsHyphens;
        }
        public String getValueWithLongNameThatNeedsHyphens() {
            return valueWithLongNameThatNeedsHyphens;
        }
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
       SimpleObject simpleObject = new SimpleObject("helloWorld", "fooBar");
       String outputWithHyphens = objectMapper.writeValueAsString(simpleObject);
       assertTrue(outputWithHyphens.contains("key-with-long-name-that-needs-hyphens"));
    }
}
