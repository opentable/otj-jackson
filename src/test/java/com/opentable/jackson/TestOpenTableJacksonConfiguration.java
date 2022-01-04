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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    private static class MapHolder {
        private Map<String, Object> map;
        private String foo;
        public MapHolder(Map<String, Object> map, String  foo) {
            this.map = map;
            this.foo = foo;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public String getFoo() {
            return foo;
        }
    }
    @Test
    public void testMapNull() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("string", "value");
        map.put("int", 1);
        map.put("null1", null);
        map.put(null, null);
        final MapHolder mapHolder = new MapHolder(map, "mike");
        // Null keys and values are dropped.
        Assert.assertEquals("{\"map\":{\"string\":\"value\",\"int\":1},\"foo\":\"mike\"}", mapper.writeValueAsString(mapHolder));
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
