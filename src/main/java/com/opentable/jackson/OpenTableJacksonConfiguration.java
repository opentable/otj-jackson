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

import java.text.DateFormat;
import java.util.Set;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTableJacksonConfiguration
{
    public enum JacksonTimeFormat {
        MILLIS, ISO8601;
    }

    @Value("${ot.jackson.time-format:ISO8601}")
    JacksonTimeFormat timeFormat = JacksonTimeFormat.ISO8601;

    @Value("${ot.jackson.afterburner:#{false}}")
    private boolean enableAfterBurner;

    @Value("${ot.jackson.mrbean:#{false}}")
    private boolean enableMrBean;

    @Value("${ot.jackson.relaxed-parser:#{false}}")
    private boolean relaxedParser = false;

    @Value("${ot.jackson.register-module-beans:#{true}}")
    private boolean registerModuleBeans;

    @Autowired
    private ObjectProvider<Module> modules = null;

    @Autowired(required = false)
    private Set<OpenTableJacksonCustomizer> customizerSet = null;

    // See https://github.com/FasterXML/jackson-databind/issues/2643 for why the custom dateformat
    private DateFormat dateFormat = new StdDateFormat().withColonInTimeZone(false);


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().setDateFormat(dateFormat);

        mapper.registerModules( guavaModule(),
                                javaTimeModule(),
                                jdk8Module(),
                                parameterNamesModule());
        if (enableMrBean) {
            mapper.registerModule(mrBeanModule());
        }
        if (enableAfterBurner) {
            mapper.registerModule(afterburnerModule());
        }
        if (registerModuleBeans && (modules != null)) {
            mapper.registerModules(modules);
        }

        // This needs to be set, otherwise the mapper will fail on every new property showing up.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Don't write out nulls by default -- if you really want them, you can change it with setOptions later.
        //MJB: old deprecated version
        //mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
       mapper.configOverride(Map.class).setInclude(JsonInclude.Value.construct(Include.NON_NULL, Include.NON_NULL));

        // No need to flush after every value, which cuts throughput by ~30%
        mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);

        // Awful JAXB shit
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);

        switch(timeFormat) {
        case MILLIS:
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, Boolean.TRUE);
            break;
        case ISO8601:
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, Boolean.FALSE);
            break;
        default:
            throw new IllegalStateException("Unknown time format: " +timeFormat);
        }

        // by default, don't serialize null values.
        mapper.setSerializationInclusion(Include.NON_NULL);

        // Relaxed parsing
        if (relaxedParser) {
            // Single quotes
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            // Unquoted field names
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        }

        if (customizerSet != null && !customizerSet.isEmpty()) {
            customizerSet.forEach(customizer -> {
                customizer.accept(mapper);
            });
        }

        return mapper;
    }

    GuavaModule guavaModule() {
        return new GuavaModule();
    }

    JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    MrBeanModule mrBeanModule() {
        return new MrBeanModule();
    }

    AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    Jdk8Module jdk8Module() {
        return new Jdk8Module();
    }

    ParameterNamesModule parameterNamesModule() {
        return new ParameterNamesModule();
    }

    public OpenTableJacksonConfiguration setEnableAfterBurner(final boolean enableAfterBurner) {
        this.enableAfterBurner = enableAfterBurner;
        return this;
    }

    public OpenTableJacksonConfiguration setEnableMrBean(final boolean enableMrBean) {
        this.enableMrBean = enableMrBean;
        return this;
    }

    public OpenTableJacksonConfiguration setRelaxedParser(final boolean relaxedParser) {
        this.relaxedParser = relaxedParser;
        return this;
    }

    public OpenTableJacksonConfiguration setTimeFormat(final JacksonTimeFormat timeFormat) {
        this.timeFormat = timeFormat;
        return this;
    }

    public OpenTableJacksonConfiguration setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public OpenTableJacksonConfiguration setRegisterModuleBeans(boolean registerModuleBeans) {
        this.registerModuleBeans = registerModuleBeans;
        return this;
    }

    OpenTableJacksonConfiguration setModules(ObjectProvider<Module> modules) {
        this.modules = modules;
        return this;
    }
}
