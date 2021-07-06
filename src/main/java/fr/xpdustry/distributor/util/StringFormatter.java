package fr.xpdustry.distributor.util;

import arc.struct.*;


public class StringFormatter{
    public final ObjectMap<String, String> map;

    public StringFormatter(){
        this.map = new ObjectMap<>();
    }

    public StringFormatter(ObjectMap<String, String> map){
        this.map = map;
    }

    public String format(String text, final Object... args){
        StringBuilder builder = new StringBuilder(text.length() * 2);
        format(text, builder, 0, text.length(), new int[]{0}, new int[]{0}, args);
        return builder.toString();
    }

    private void format(String text, StringBuilder builder, int start, int end, final int[] index, final int[] depth, final Object... args){
        int pos = start;
        boolean ignore = false;

        while(pos < end){
            char c = text.charAt(pos);

            // Ignore the next character
            if(c == '\\' && !ignore){
                ignore = true;
            }
            // Replace the @ with the next argument, like Strings.format
            else if(c == '@' && index[0] < args.length && !ignore){
                builder.append(args[index[0]++]);
            }
            // Replace the key between the braces with the value inside the map
            else if(c == '{' && !ignore){
                int close = text.indexOf('}', pos);

                if(close != -1){
                    String key = text.substring(pos + 1, close);
                    builder.append(map.get(key, key));
                    pos = close;
                }
            }
            // Close the text between color tags, uses recursion for nested tokens
            else if(c == '$' && !ignore){
                int open = text.indexOf('[', pos);

                if(open != -1){
                    int close = text.indexOf(']', open);

                    if(close != -1){
                        String key = text.substring(pos + 1, open);
                        depth[0]++;

                        builder.append(map.get(key, key));
                        format(text, builder, open + 1, close + 1, index, depth, args);

                        pos = close;
                    }
                }
            }
            // Closes the color tag when a $ token has been closed
            else if(c == ']' && depth[0] > 0 && !ignore){
                builder.append("[]");
                depth[0]--;
            }else{
                builder.append(c);
                ignore = false;
            }

            pos++;
        }
    }
}
