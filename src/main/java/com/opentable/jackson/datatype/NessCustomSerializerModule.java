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
package com.opentable.jackson.datatype;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import com.opentable.jackson.OpenTableObjectMapperBinder;

public class NessCustomSerializerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(new TypeLiteral<JsonDeserializer<UUID>>() {}).to(CustomUuidDeserializer.class);
        bind(new TypeLiteral<JsonSerializer<UUID>>() {}).to(CustomUuidSerializer.class);

        OpenTableObjectMapperBinder.bindJacksonModule(binder()).to(CustomUuidModule.class).in(Scopes.SINGLETON);
        OpenTableObjectMapperBinder.bindJacksonModule(binder()).to(MapEntryModule.class).in(Scopes.SINGLETON);
        OpenTableObjectMapperBinder.bindJacksonModule(binder()).to(CommonsLang3Module.class).in(Scopes.SINGLETON);
    }
}