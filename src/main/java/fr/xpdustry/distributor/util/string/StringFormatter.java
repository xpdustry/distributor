package fr.xpdustry.distributor.util.string;


public interface StringFormatter{
    StringFormatter VOID = (text, args) -> text;

    StringFormatter DEFAULT = String::format;

    StringFormatter OBJECT = (text, args) -> {
        if(args.length <= 0){
            return text;
        }else{
            int index = 0;
            StringBuilder out = new StringBuilder(text.length() + args.length * 2);

            for(int i = 0; i < text.length(); ++i){
                char c = text.charAt(i);
                if(c == '@' && index < args.length){
                    out.append(args[index++]);
                }else{
                    out.append(c);
                }
            }

            return out.toString();
        }
    };

    /** @return a formatted string with the given arguments */
    String format(String text, Object... args);
}
