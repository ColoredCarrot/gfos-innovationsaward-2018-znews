package com.coloredcarrot.jsonapi.reflect;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * If a class implementing {@link JsonSerializable} has to execute logic
 * when deserializing JSON, a custom deserializer can be provided by annotating
 * a static method with @JsonDeserializer.<br>
 * This method (i.e. the class <i>deserializer</i>) must have the following
 * signature:
 * <pre><code>&lt;ANY-ACCESS-MODIFIER&gt; static &lt;THE-CLASS&gt; (JsonNode)</code></pre>
 * <br>
 * Classes should specify a deserializer if they contain a non-transient {@link Map} either
 * whose keys or values are of a non-primitive type: The default deserializer will otherwise
 * guess the type of the keys/values using {@link Json#deserializeFreely(JsonNode) free deserialization}.<br>
 * <br>
 * It is recommended that classes providing a custom deserializer also provide a
 * custom @{@link JsonSerializer}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDeserializer
{
}
