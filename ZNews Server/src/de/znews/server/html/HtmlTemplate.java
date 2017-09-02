package de.znews.server.html;

import de.znews.server.util.Str;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class HtmlTemplate implements Function<Map<String, Object>, Str>
{
    
    private final Str template;
    
    public Str apply(Map<String, Object> arguments)
    {
        Str result = template.clone();
        arguments.forEach((key, value) -> result.replaceOnce(key, value.toString()));
        return result;
    }
    
}
