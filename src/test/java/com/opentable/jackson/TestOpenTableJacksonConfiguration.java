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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

public class TestOpenTableJacksonConfiguration
{
    final ObjectMapper mapper = new OpenTableJacksonConfiguration().objectMapper();

    @Test
    public void testSimple() {
        Assert.assertNotNull(mapper);
    }

    @Test
    public void testParameterNames() throws Exception {
        MrBean mrBean = mapper.readValue("{\"bar\":\"1\",\"foo\":\"2\"}", MrBean.class);
        Assert.assertEquals("1", mrBean.bar);
        Assert.assertEquals("2", mrBean.foo);
    }

    @Test
    public void testBadParameterName() throws Exception {
        MrBean mrBean = mapper.readValue("{\"bax\":\"1\",\"foo\":\"2\"}", MrBean.class);
        Assert.assertNull(mrBean.bar);
        Assert.assertEquals("2", mrBean.foo);
    }

    public static class MrBean {
        final String foo, bar;

        @JsonCreator
        public MrBean(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
}
