package com.coloredcarrot.jsonapi;

import com.coloredcarrot.jsonapi.ast.JsonArray;
import com.coloredcarrot.jsonapi.ast.JsonBoolean;
import com.coloredcarrot.jsonapi.ast.JsonDecimal;
import com.coloredcarrot.jsonapi.ast.JsonInteger;
import com.coloredcarrot.jsonapi.ast.JsonNode;
import com.coloredcarrot.jsonapi.ast.JsonNull;
import com.coloredcarrot.jsonapi.ast.JsonObject;
import com.coloredcarrot.jsonapi.ast.JsonString;
import com.coloredcarrot.jsonapi.generation.JsonGenerator;
import com.coloredcarrot.jsonapi.generation.JsonOutputStream;
import com.coloredcarrot.jsonapi.generation.PrettyJsonGenerator;
import com.coloredcarrot.jsonapi.parsing.JsonInputStream;
import com.coloredcarrot.jsonapi.parsing.JsonParser;
import com.coloredcarrot.jsonapi.parsing.lex.JsonLexer;
import com.coloredcarrot.jsonapi.reflect.ReflectJsonDeserializer;
import com.coloredcarrot.jsonapi.reflect.ReflectJsonSerializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Json
{
    
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    private static ReflectJsonSerializer serializer;
    
    public static ReflectJsonSerializer getSerializer()
    {
        if (serializer == null)
            serializer = new ReflectJsonSerializer();
        return serializer;
    }
    
    private static ReflectJsonDeserializer deserializer;
    
    public static ReflectJsonDeserializer getDeserializer()
    {
        if (deserializer == null)
            deserializer = new ReflectJsonDeserializer();
        return deserializer;
    }
    
    public static JsonInputStream getInputStream(InputStream source)
    {
        return getInputStream(source, DEFAULT_CHARSET);
    }
    
    public static JsonInputStream getInputStream(InputStream source, Charset cs)
    {
        return getInputStream(new InputStreamReader(source, cs));
    }
    
    public static JsonInputStream getInputStream(Reader source)
    {
        return new JsonParser(new JsonLexer(source));
    }
    
    public static JsonOutputStream getOutputStream(OutputStream target)
    {
        return getOutputStream(target, DEFAULT_CHARSET);
    }
    
    public static JsonOutputStream getOutputStream(OutputStream target, boolean enablePrettyPrinting)
    {
        return getOutputStream(target, DEFAULT_CHARSET, enablePrettyPrinting);
    }
    
    public static JsonOutputStream getOutputStream(OutputStream target, Charset cs)
    {
        return getOutputStream(new OutputStreamWriter(target, cs));
    }
    
    public static JsonOutputStream getOutputStream(OutputStream target, Charset cs, boolean enablePrettyPrinting)
    {
        return getOutputStream(new OutputStreamWriter(target, cs), enablePrettyPrinting);
    }
    
    public static JsonOutputStream getOutputStream(Writer target)
    {
        return new JsonGenerator(target);
    }
    
    public static JsonOutputStream getOutputStream(Writer target, boolean enablePrettyPrinting)
    {
        return enablePrettyPrinting ? new PrettyJsonGenerator(target) : new JsonGenerator(target);
    }
    
    public static JsonNode serialize(Object obj)
    {
        return getSerializer().serialize(obj);
    }
    
    public static <T> T deserialize(JsonNode json, Class<T> clazz)
    {
        return getDeserializer().deserialize(clazz, json);
    }
    
    public static String toString(JsonNode jsonNode)
    {
        StringWriter out = new StringWriter();
        getOutputStream(out).write(jsonNode);
        return out.toString();
    }
    
    public static String toString(Object obj)
    {
        return toString(serialize(obj));
    }
    
    public static <T> T deserializeFromString(String json, Class<T> clazz)
    {
        return deserialize(getInputStream(new StringReader(json)).next(), clazz);
    }
    
    /**
     * Performs a <i>free deserialization</i> of the specified JsonNode.<br>
     * <br>
     * During this process, the deserializer guesses the return type by looking
     * at the type of the node, following these rules:
     * <ol>
     * <li>{@link JsonString} -> {@link String}</li>
     * <li>{@link JsonInteger} -> {@link Long}</li>
     * <li>{@link JsonDecimal} -> {@link Double}</li>
     * <li>{@link JsonBoolean} -> {@link Boolean}</li>
     * <li>{@link JsonArray} -> {@link List}{@code <Object>}</li>
     * <li>{@link JsonObject} -> {@link Map}{@code <String, Object>}</li>
     * <li>{@link JsonNull} -> {@code null}</li>
     * </ol>
     * Note that for the containers ({@link JsonArray} and {@link JsonObject}),
     * the element type is <code>Object</code>. The contents of the
     * container are also freely deserialized.
     *
     * @param json The JsonNode which is to be freely deserialized
     * @return An Object or {@code null}, following the rules of free deserialization.
     */
    public static Object deserializeFreely(JsonNode json)
    {
        return getDeserializer().freelyDeserialize(json);
    }
    
    public static JsonInteger serialize(byte b)
    {
        return new JsonInteger(b);
    }
    
    public static JsonInteger serialize(short s)
    {
        return new JsonInteger(s);
    }
    
    public static JsonInteger serialize(int i)
    {
        return new JsonInteger(i);
    }
    
    public static JsonInteger serialize(long l)
    {
        return new JsonInteger(l);
    }
    
    public static JsonDecimal serialize(float f)
    {
        return new JsonDecimal(f);
    }
    
    public static JsonDecimal serialize(double d)
    {
        return new JsonDecimal(d);
    }
    
    public static JsonBoolean serialize(boolean b)
    {
        return new JsonBoolean(b);
    }
    
    public static JsonString serialize(char c)
    {
        return new JsonString(Character.toString(c));
    }
    
    public static String escape(String s)
    {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    public static String unescape(String s)
    {
        return s.replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
    
}
