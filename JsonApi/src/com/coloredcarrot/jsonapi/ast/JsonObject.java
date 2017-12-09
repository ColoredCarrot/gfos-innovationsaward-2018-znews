package com.coloredcarrot.jsonapi.ast;

import com.coloredcarrot.jsonapi.Json;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonContainer
{
    
    private Map<String, JsonNode> contents;
    private transient Builder builder;
    
    public JsonObject()
    {
        this.contents = new HashMap<>();
    }
    
    public JsonObject(Map<String, JsonNode> contents)
    {
        this.contents = new HashMap<>(contents);
    }
    
    public JsonObject add(String key, JsonNode node)
    {
        contents.put(key, node);
        return this;
    }
    
    public JsonNode remove(String key)
    {
        return contents.remove(key);
    }
    
    public Map<String, JsonNode> getMappings()
    {
        return Collections.unmodifiableMap(contents);
    }
    
    @Override
    public Collection<JsonNode> getContents()
    {
        return Collections.unmodifiableCollection(contents.values());
    }
    
    @Override
    public int size()
    {
        return contents.size();
    }
    
    public Set<String> getKeys()
    {
        return contents.keySet();
    }
    
    public JsonNode get(String key)
    {
        return contents.get(key);
    }
    
    public JsonObject getObject(String key)
    {
        return (JsonObject) get(key);
    }
    
    public JsonArray getArray(String key)
    {
        return (JsonArray) get(key);
    }
    
    public JsonContainer getContainer(String key)
    {
        return (JsonContainer) get(key);
    }
    
    public JsonNumber getNumber(String key)
    {
        return (JsonNumber) get(key);
    }
    
    public JsonNumber getPrimitive(String key)
    {
        return (JsonNumber) get(key);
    }
    
    public JsonNode getOrNull(String key)
    {
        return contents.getOrDefault(key, JsonNull.INSTANCE);
    }
    
    public Builder builder()
    {
        if (builder == null)
            builder = new Builder();
        return builder;
    }
    
    public static Builder createBuilder()
    {
        return new JsonObject().builder();
    }
    
    public class Builder
    {
    
        private Builder a(String key, JsonNode value)
        {
            contents.put(key, value);
            return this;
        }
    
        private JsonNode g(String key)
        {
            return JsonObject.this.get(key);
        }
    
        public Builder add(String key, Object value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, byte value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, short value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, int value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, long value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, float value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, double value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, boolean value)
        {
            return a(key, Json.serialize(value));
        }
        
        public Builder add(String key, char value)
        {
            return a(key, Json.serialize(value));
        }
    
        public Builder remove(String key)
        {
            contents.remove(key);
            return this;
        }
    
        public <T> T get(String key, Class<T> clazz)
        {
            return contents.containsKey(key) ? Json.deserialize(contents.get(key), clazz) : null;
        }
    
        public String getString(String key)
        {
            return g(key).stringValue();
        }
    
        public int getInt(String key)
        {
            return g(key).intValue();
        }
    
        public double getDouble(String key)
        {
            return g(key).doubleValue();
        }
    
        public JsonObject get()
        {
            return JsonObject.this;
        }
        
        public JsonObject build()
        {
            builder = null;
            return JsonObject.this;
        }
        
    }
    
}
