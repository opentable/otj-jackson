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

import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

public class TestCustomUuidModule
{
    // This test ensures that the CustomUuidModule is correctly installed
    @Test
    public void testCustomUUIDDeserialization() throws Exception {
        final UUID orig = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ObjectMapper mapper = getObjectMapper();
        UUID uuid = mapper.readValue('"' + orig.toString() + '"', new TypeReference<UUID>(){});
        Assert.assertEquals(orig, uuid);
    }

    @Test
    public void testCustomUUIDSerialization() throws Exception {
        ObjectMapper mapper = getObjectMapper();
        final UUID id = new UUID(9, 9);
        Assert.assertEquals('"' + id.toString() + '"', mapper.writeValueAsString(id));
    }

    @SuppressWarnings("serial")
    private ObjectMapper getObjectMapper()
    {
        return new OpenTableJacksonConfiguration().objectMapper();
    }
}
