package com.coloredcarrot.jsonapi.ast;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.reflect.JsonSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonArray extends JsonContainer
{
    
    private static final long serialVersionUID = 8921191506768984301L;
    
    private List<JsonNode> array;
    private transient Builder builder;
    
    public JsonArray()
    {
        this.array = new ArrayList<>();
    }
    
    public JsonArray(List<JsonNode> nodes)
    {
        this.array = new ArrayList<>(nodes);
    }
    
    public JsonArray add(JsonNode node)
    {
        array.add(node);
        return this;
    }
    
    public JsonNode[] getContentsAsArray()
    {
        return array.toArray(new JsonNode[0]);
    }
    
    @Override
    public Collection<JsonNode> getContents()
    {
        return Collections.unmodifiableCollection(array);
    }
    
    @Override
    public int size()
    {
        return array.size();
    }
    
    public JsonNode get(int index)
    {
        return array.get(index);
    }
    
    public boolean isSimpleArray()
    {
        if (array.isEmpty())
            return true;
        Class<? extends JsonNode> theClass = array.get(0).getClass();
        return array.stream().allMatch(j -> j.getClass() == theClass);
    }
    
    @Override
    public String toString()
    {
        return array.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "[", "]"));
    }
    
    public Builder builder()
    {
        if (builder == null)
            builder = new Builder();
        return builder;
    }
    
    public static Builder createBuilder()
    {
        return new JsonArray().builder();
    }
    
    public class Builder
    {
        
        private Builder a(JsonNode value)
        {
            array.add(value);
            return this;
        }
        
        private JsonNode g(int index)
        {
            return JsonArray.this.get(index);
        }
        
        public Builder add(Object value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(byte value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(short value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(int value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(long value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(float value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(double value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(boolean value)
        {
            return a(Json.serialize(value));
        }
        
        public Builder add(char value)
        {
            return a(Json.serialize(value));
        }
        
        public <T> T get(int index, Class<T> clazz)
        {
            return Json.deserialize(g(index), clazz);
        }
        
        public String getString(int index)
        {
            return g(index).stringValue();
        }
        
        public int getInt(int index)
        {
            return g(index).intValue();
        }
        
        public double getDouble(int index)
        {
            return g(index).doubleValue();
        }
        
        public <T> List<T> getAll(Class<T> clazz)
        {
            List<T> list = new ArrayList<>();
            for (JsonNode e : array)
                list.add(Json.deserialize(e, clazz));
            return list;
        }
        
        public JsonArray build()
        {
            return JsonArray.this;
        }
        
    }
    
    @JsonSerializer
    private JsonArray _serialize()
    {
        return this;
    }
    
}
