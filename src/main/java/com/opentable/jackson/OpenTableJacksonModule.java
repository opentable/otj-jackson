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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.opentable.config.ConfigProvider;
import com.opentable.jackson.datatype.NessCustomSerializerModule;

public final class OpenTableJacksonModule extends AbstractModule
{
    @Override
    public void configure()
    {
        // Annotated version (@Json) is also bound to json.
        bind(ObjectMapper.class).annotatedWith(JsonMapper.class).toProvider(OpenTableObjectMapperProvider.class).in(Scopes.SINGLETON);

        // Annotated version (@Smile) is bound to the smile factory.
        bind(ObjectMapper.class).annotatedWith(SmileMapper.class).toProvider(new OpenTableObjectMapperProvider(new SmileFactory())).in(Scopes.SINGLETON);

        // Default (not annotated) instance is bound to json.
        bind(ObjectMapper.class).toProvider(OpenTableObjectMapperProvider.class).in(Scopes.SINGLETON);

        bind(OpenTableJacksonConfig.class).toProvider(ConfigProvider.of(OpenTableJacksonConfig.class)).in(Scopes.SINGLETON);

        OpenTableObjectMapperBinder.bindJacksonModule(binder()).toInstance(new GuavaModule());
        OpenTableObjectMapperBinder.bindJacksonModule(binder()).toInstance(new JSR310Module());

        install (new NessCustomSerializerModule());

        // MrBean is pretty safe to globally install, since it only deserializes types that would otherwise fail.
        OpenTableObjectMapperBinder.bindJacksonModule(binder()).to(MrBeanModule.class);

        OpenTableObjectMapperBinder.bindJacksonModule(binder()).to(AfterburnerModule.class);
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj.getClass() == OpenTableJacksonModule.class;
    }
}
