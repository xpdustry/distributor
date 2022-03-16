package fr.xpdustry.distributor.script.js;

import arc.struct.*;
import fr.xpdustry.distributor.struct.*;
import java.util.*;
import java.util.regex.*;
import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;

/**
 * This is a {@link ClassShutter} backed by 2 regexes. One act as the blacklist and the other the whitelist.
 */
public final class RegexClassShutter implements ClassShutter {

  private final Seq<Pattern> blacklist;
  private final Seq<Pattern> whitelist;

  public RegexClassShutter(final @NotNull Iterable<String> blacklist, final @NotNull Iterable<String> whitelist) {
    this.blacklist = Seq.with(blacklist).map(Pattern::compile);
    this.whitelist = Seq.with(whitelist).map(Pattern::compile);
  }

  /**
   * Returns the blacklist regex list.
   */
  public @NotNull List<Pattern> getBlacklist() {
    return new ArcList<>(blacklist);
  }

  /**
   * Returns the whitelist regex list.
   */
  public @NotNull List<Pattern> getWhitelist() {
    return new ArcList<>(whitelist);
  }

  @Override
  public boolean visibleToScripts(@NotNull final String s) {
    return !(blacklist.contains(p -> p.matcher(s).matches()) && !whitelist.contains(p -> p.matcher(s).matches()));
  }
}
