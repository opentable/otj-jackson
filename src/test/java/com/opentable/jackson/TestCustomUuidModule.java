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

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.junit.Assert;
import org.junit.Test;

public class TestCustomUuidModule
{
    final AtomicBoolean serCalled = new AtomicBoolean();
    final AtomicBoolean deserCalled = new AtomicBoolean();

    // This test ensures that the CustomUuidModule is correctly installed
    @Test
    public void testCustomUUIDDeserialization() throws Exception {
        final UUID orig = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ObjectMapper mapper = getObjectMapper();
        UUID uuid = mapper.readValue('"' + orig.toString() + '"', new TypeReference<UUID>(){});
        Assert.assertEquals(orig, uuid);
        Assert.assertTrue(deserCalled.get());
    }

    @Test
    public void testCustomUUIDSerialization() throws Exception {
        ObjectMapper mapper = getObjectMapper();
        final UUID id = new UUID(9, 9);
        Assert.assertEquals('"' + id.toString() + '"', mapper.writeValueAsString(id));
        Assert.assertTrue(serCalled.get());
    }

    @SuppressWarnings("serial")
    private ObjectMapper getObjectMapper()
    {
        return new OpenTableJacksonConfiguration() {
            @Override
            CustomUuidModule customUuidModule() {
                return new CustomUuidModule() {
                    @Override
                    CustomUuidSerializer serializer() {
                        return new CustomUuidSerializer() {
                            @Override
                            public void serialize(UUID value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                                serCalled.set(true);
                                super.serialize(value, jgen, provider);
                            }
                        };
                    }
                    @Override
                    CustomUuidDeserializer deserializer() {
                        return new CustomUuidDeserializer() {
                            @Override
                            public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                                deserCalled.set(true);
                                return super.deserialize(p, ctxt);
                            }
                        };
                    }
                };
            }
        }.objectMapper();
    }
}
