package fr.xpdustry.distributor.util;

import arc.util.*;


public interface Formatter{
    Formatter defaultFormatter = Strings::format;

    String format(String text, Object... args);
}
