package fr.xpdustry.distributor.core.util;

import arc.util.*;


public interface Formatter{
    Formatter defaultFormatter = Strings::format;

    /** @return a formatted string with the given arguments */
    String format(String text, Object... args);
}
