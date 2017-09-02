package com.coloredcarrot.jsonapi.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a class implementing {@link JsonSerializable} has specific requirements
 * for the generated JSON, a custom serializer can be provided by annotating
 * a non-static method with @JsonSerializer.<br>
 * This method (i.e. the class <i>serializer</i>) must have the following
 * signature:
 * <pre><code>&lt;ANY-ACCESS-MODIFIER&gt; JsonNode ()</code></pre>
 * <br>
 * It is recommended that classes providing a custom serializer also provide a
 * custom @{@link JsonDeserializer}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSerializer
{
}
