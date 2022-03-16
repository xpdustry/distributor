package fr.xpdustry.distributor.script.js;

import arc.struct.*;
import fr.xpdustry.distributor.struct.*;
import java.util.*;
import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;

/**
 * This is a {@link ClassShutter} backed by 2 regexes.
 * One act as the blacklist and the other the whitelist.
 */
public final class ListClassShutter implements ClassShutter {

  private final Seq<String> blacklist;
  private final Seq<String> whitelist;

  public ListClassShutter(final @NotNull Iterable<String> blacklist, final @NotNull Iterable<String> whitelist) {
    this.blacklist = Seq.with(blacklist);
    this.whitelist = Seq.with(whitelist);
  }

  @Override
  public boolean visibleToScripts(@NotNull final String s) {
    return !(blacklist.contains(s::startsWith) && !whitelist.contains(s::startsWith));
  }

  /**
   * Returns the blacklist regex list.
   */
  public @NotNull List<String> getBlacklist() {
    return new ArcList<>(blacklist);
  }

  /**
   * Returns the whitelist regex list.
   */
  public @NotNull List<String> getWhitelist() {
    return new ArcList<>(whitelist);
  }
}
