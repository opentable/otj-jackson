package com.opentable.jackson;

import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Can use to customize the ObjectMapper - just inject a bean of OpenTableJacksonCustomizer
 */
public interface OpenTableJacksonCustomizer extends Consumer<ObjectMapper> {

}
