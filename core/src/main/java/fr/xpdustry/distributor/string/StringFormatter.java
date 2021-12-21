package fr.xpdustry.distributor.string;

import arc.struct.*;
import arc.util.*;

import java.util.*;


@FunctionalInterface
public interface StringFormatter{
    String format(String string, Object... args);

    Map<String, String> SERVER_COLORS = Map.of(
        "black",    ColorCodes.black,
        "white",    ColorCodes.white,
        "red",      ColorCodes.red,
        "green",    ColorCodes.green,
        "yellow",   ColorCodes.yellow,
        "blue",     ColorCodes.blue,
        "purple",   ColorCodes.purple,
        "cyan",     ColorCodes.cyan
    );

    StringFormatter DEFAULT = String::format;

    StringFormatter MINDUSTRY_DEFAULT = Strings::format;

    StringFormatter MINDUSTRY_SERVER = (string, args) -> {
        if(string.isBlank()) return string;

        final var builder = new StringBuilder(string.length());
        final var stack = new Seq<String>(4);
        int sIndex = 0, aIndex = 0;

        while(sIndex < string.length()){
            char c = string.charAt(sIndex);

            if(c == '@' && aIndex < args.length){
                builder.append(args[aIndex++]);
            }else if(c == '['){
                boolean hasNext = sIndex + 1 < string.length();

                if(hasNext && string.charAt(sIndex + 1) == '['){
                    builder.append("[[");
                    sIndex++;
                }else if(hasNext && string.charAt(sIndex + 1) == ']'){
                    builder.append(stack.size > 1 ? stack.pop() : "");
                    sIndex++;
                }else{
                    int end = string.indexOf(']', sIndex + 1);
                    if(end != -1){
                        var color = SERVER_COLORS.get(string.substring(sIndex + 1, end));

                        if(color != null){
                            builder.append(color);
                            stack.add(color);
                            sIndex = end;
                        }else{
                            builder.append(c);
                        }
                    }
                }
            }else{
                builder.append(c);
            }

            sIndex++;
        }

        return builder.toString();
    };
}
