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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Jackson JSON encoding. See {@link #objectMapper()} for details on our customized configuration.
 */
@Configuration
public class OpenTableJacksonConfiguration
{
    /**
     * Formats that can be used to encoding time in JSON
     */
    public enum JacksonTimeFormat {

        /**
         * Milliseconds since <a href="https://en.wikipedia.org/wiki/Unix_time">the epoch</a>
         */
        MILLIS,

        /**
         * A string with time encoded per ISO standard 8601.
         * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">Wikipedia: ISO-8601</a>
         */
        ISO8601;
    }

    /**
     * The format that should be used for JSON time encoding. Can be configured via <tt>ot.jackson.time-format</tt>. Defaults to ISO-8601.
     */
    @Value("${ot.jackson.time-format:ISO8601}")
    JacksonTimeFormat timeFormat = JacksonTimeFormat.ISO8601;

    /**
     * Should Afterburner be enabled? Afterburner adds dynamic bytecode generation to improve performance.
     * Can be configured via <tt>ot.jackson.afterburner</tt>. It defaults to false.
     */
    @Value("${ot.jackson.afterburner:#{false}}")
    private boolean enableAfterBurner;

    /**
     * Should Mr Bean be enabled? Mr Bean is an extension that implements support for "POJO type materialization";
     * ability for databinder to construct implementation classes for Java interfaces and abstract classes, as part of deserialization.
     * Can be configured via <tt>ot.jackson.mrbean</tt>. It defaults to false.
     */
    @Value("${ot.jackson.mrbean:#{false}}")
    private boolean enableMrBean;

    /**
     * Create and expose the object mapper bean configured with OpenTable's customizations:
     *  <ul>
     *  <li>We won't fail on deserielizing an unknown property (rather they will be ignored)</li>
     *  <li>Serialization inclusion is set to @{@link Include.NON_NULL} and {@link SerializationFeature.WRITE_NULL_MAP_VALUES} is disabled, we won't write out nulls</li>
     *  <li>{@link SerializationFeature.FLUSH_AFTER_WRITE_VALUE} is disabled, we won't flush after write for performance reasons</li>
     *  <li>{@link MapperFeature.USE_GETTERS_AS_SETTERS} is disabled, we won't write maps and collections using getters like JAX-B does</li>
     *  <li>Mr. Bean and the Afterburner modules will be enabled if configured</li>
     *  <li>The following modules will always be enabled:
     *  <ul>
     *    <li>Guava, for handling Guava collections</li>
     *    <li>Java Time, for handling Java 8 time types</li>
     *    <li>JDK 8, for handling types like Optional</li>
     *    <li>Parameter names, for detecting constructor and factory method ("creator") parameters without having to use <tt>@JsonProperty</tt> annotation</li>
     *    </li>
     *    </ul>
     *
     * @return a customized object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

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

        // This needs to be set, otherwise the mapper will fail on every new property showing up.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Don't write out nulls by default -- if you really want them, you can change it with setOptions later.
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

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

        return mapper;
    }

    /**
     * Module to support JSON serialization and deserialization of Guava data types.
     * @return the Guava module
     */
    GuavaModule guavaModule() {
        return new GuavaModule();
    }

    /**
     * Datatype module to make Jackson recognize Java 8 Date & Time API data types (JSR-310).
     * @return the Java Time Module
     */
    JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    /**
     * Mr Bean is an extension that implements support for "POJO type materialization"; ability for databinder to construct implementation classes for Java interfaces and abstract classes, as part of deserialization.
     * @return the mr. Bean module
     */
    MrBeanModule mrBeanModule() {
        return new MrBeanModule();
    }

    /**
     * Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers, eliminating majority of remaining data binding overhead.
     * @return The afterburner module
     */
    AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    /**
     * Jackson module that adds supports for JDK datatypes included in version 8, e.g. Optional
     * @return the JDK 8 Module
     */
    Jdk8Module jdk8Module() {
        return new Jdk8Module();
    }

    /**
     * Jackson module that adds support for accessing parameter names; a feature added in JDK 8.
     * @return the Parameter Names Module
     */
    ParameterNamesModule parameterNamesModule() {
        return new ParameterNamesModule();
    }
}
