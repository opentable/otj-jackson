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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

class CustomUuidModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    CustomUuidModule() {
        super("CustomUuidModule", new Version(2, 0, 0, null, "com.opentable.components", "otj-jackson/CustomUuidModule"));
        addDeserializer(UUID.class, deserializer());
        addSerializer(UUID.class, serializer());
    }

    CustomUuidSerializer serializer() {
        return new CustomUuidSerializer();
    }

    CustomUuidDeserializer deserializer() {
        return new CustomUuidDeserializer();
    }
}