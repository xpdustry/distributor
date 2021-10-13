package fr.xpdustry.distributor.util.string;

import java.util.*;


@SuppressWarnings("unused")
public class Interpolator implements StringFormatter{
    private final Map<String, String> map;

    public Interpolator(Map<String, String> map){
        this.map = Objects.requireNonNull(map);
    }

    public String replace(String key, String value){
        return map.replace(key, value);
    }

    public boolean has(String key, String value){
        return map.containsKey(key);
    }

    @Override
    public String format(String text, Object... args){
        if(text == null || text.isEmpty()) return "";

        StringBuilder builder = new StringBuilder(text.length() * 2);

        int pos = 0;
        int index = 0; // <- Index of the args
        int nextToken = text.indexOf(text);

        while(pos < text.length()){
            char c = text.charAt(pos);

            // Replace the @ with the next argument, like Strings.format
            if(c == '@' && index < args.length){
                builder.append(args[index++]);
            }
            // Replace the key between the braces with the value inside the map
            else if(c == '{'){
                int end = text.indexOf('}', pos);

                if(end != -1){
                    String key = text.substring(pos + 1, end);
                    builder.append(map.getOrDefault(key, key));
                    pos = end;
                    nextToken = text.indexOf('}', end + 1);
                }
            }else if(c == '}'){
                nextToken = text.indexOf('}', nextToken + 1);
            }else{
                builder.append(c);
            }

            pos++;
        }

        return builder.toString();
    }
}
