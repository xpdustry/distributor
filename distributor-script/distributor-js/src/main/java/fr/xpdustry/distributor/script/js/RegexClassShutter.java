package fr.xpdustry.distributor.script.js;

import org.checkerframework.checker.nullness.qual.*;
import rhino.*;

import java.util.regex.*;


public class RegexClassShutter implements ClassShutter{
    private final Pattern blacklist;
    private final Pattern whitelist;

    public RegexClassShutter(@NonNull Iterable<String> blacklist, @NonNull Iterable<String> whitelist){
        this.blacklist = Pattern.compile(String.join("|", blacklist));
        this.whitelist = Pattern.compile(String.join("|", whitelist));
    }

    @Override public boolean visibleToScripts(String s){
        return !(blacklist.matcher(s).matches() && !whitelist.matcher(s).matches());
    }
}
