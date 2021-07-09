package fr.xpdustry.distributor.core.util;

import arc.struct.*;
import arc.util.*;

import java.util.*;


public class StringFormatter implements Formatter{
    protected final ObjectMap<String, String> map;

    /**
     * Create a StringFormatter, a tool specifically designed for plugins.
     * <br>It can store predefined variables for templating your plugin messages and format strings like {@link Strings#format}.
     */
    public StringFormatter(){
        this.map = new ObjectMap<>();
    }

    public StringFormatter(ObjectMap<String, String> map){
        this.map = Objects.requireNonNull(map, "The map is null.");
    }

    public String add(String key, String value){
        return map.put(key, value);
    }

    public boolean has(String key, String value){
        return map.containsKey(key);
    }

    public String remove(String key){
        return map.remove(key);
    }

    /**
     * Behaves like {@link Strings#format}, but can also use predefined values when used with
     * { and } tokens.
     *
     * <br>For example, if you have the predefined variable [age=21], you just have to do:
     * <blockquote><pre>
     * Bob is {age} years old.
     * </pre></blockquote>
     *
     * this will output:
     * <blockquote><pre>
     * Bob is 21 years old.
     * </pre></blockquote>
     *
     * <br>The $ token tells to the Formatter to encapsulate the content of the closure. It is intended to be used with color tags.
     * For example, the predefined variable [r=red] can be used such as:
     * <blockquote><pre>
     * This is $r{really} important.
     * </pre></blockquote>
     *
     * this will output:
     * <blockquote><pre>
     * This is [red]really[] important.
     * </pre></blockquote>
     * This token can be nested.
     *
     * <br><br>If you want to use the tokens as regular characters, make sure to you escape them with \ . Such as:
     * <blockquote><pre>
     * This is a nested \{ $red{closure} \}.
     * </pre></blockquote>
     * this will output:
     * <blockquote><pre>
     * This is a nested { [red]closure[] }.
     * </pre></blockquote>
     */
    @Override
    public String format(String text, final Object... args){
        if(text == null || text.isEmpty()) return "";

        StringBuilder builder = new StringBuilder(text.length() * 2);

        int pos = 0;
        int index = 0; // <- Index of the args
        int depth = 0; // <- Nesting level of the closures

        int nextToken = text.indexOf(text);
        boolean hadIgnoreToken = false;

        while(pos < text.length()){
            char c = text.charAt(pos);

            // Ignore the next character
            if(c == '\\' && !hadIgnoreToken){
                hadIgnoreToken = true;
            }
            // Replace the @ with the next argument, like Strings.format
            else if(c == '@' && index < args.length && !hadIgnoreToken){
                builder.append(args[index++]);
            }
            // Replace the key between the braces with the value inside the map
            else if(c == '{' && !hadIgnoreToken){
                int end = text.indexOf('}', pos);

                if(end != -1){
                    String key = text.substring(pos + 1, end);
                    builder.append(map.get(key, key));
                    pos = end;
                    nextToken = text.indexOf('}', end + 1);
                }
            }
            // Replace the token with an auto-closing color tag
            else if(c == '$' && nextToken != -1 && !hadIgnoreToken){
                int start = text.indexOf('{', pos);

                if(start != -1){
                    String key = text.substring(pos + 1, start);
                    builder.append(map.get(key, key));
                    pos = start;
                    depth++;
                }
            }
            //
            else if(c == '}' && depth > 0 && !hadIgnoreToken){
                builder.append("[]");
                nextToken = text.indexOf('}', nextToken + 1);
                depth--;
            }else{
                builder.append(c);
                hadIgnoreToken= false;
            }

            pos++;
        }

        return builder.toString();
    }
}
