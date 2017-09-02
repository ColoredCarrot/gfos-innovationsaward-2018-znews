package com.coloredcarrot.jsonapi.reflect;

/**
 * Classes should implement this interface to indicate they are intended
 * to be persistently stored using the JSON format.<br>
 * Although implementing JsonSerializable is not a direct requirement
 * for JSON-(de)serialization, it is strongly recommended to make use of
 * this interface as documentation to future developers.<br>
 * <br>
 * Classes wishing to provide a custom @{@link JsonSerializer} or @{@link JsonDeserializer}
 * <b>must</b> implement this interface.
 */
public interface JsonSerializable
{
}
