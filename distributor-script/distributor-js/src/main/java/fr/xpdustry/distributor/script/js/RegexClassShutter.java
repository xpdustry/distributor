package fr.xpdustry.distributor.script.js;

import org.checkerframework.checker.nullness.qual.*;
import rhino.*;

import java.util.regex.*;


/**
 * This is a {@link ClassShutter} backed by 2 regexes.
 * One act as the blacklist and the other the whitelist.
 */
public final class RegexClassShutter implements ClassShutter{
    private final Pattern blacklist;
    private final Pattern whitelist;

    public RegexClassShutter(@NonNull Iterable<String> blacklist, @NonNull Iterable<String> whitelist){
        this.blacklist = Pattern.compile(String.join("|", blacklist));
        this.whitelist = Pattern.compile(String.join("|", whitelist));
    }

    /** @return the blacklist regex */
    public @NonNull Pattern getBlacklist(){
        return blacklist;
    }

    /** @return the whitelist regex */
    public @NonNull Pattern getWhitelist(){
        return whitelist;
    }

    @Override public boolean visibleToScripts(@NonNull String s){
        return !(blacklist.matcher(s).matches() && !whitelist.matcher(s).matches());
    }
}
