package fr.xpdustry.distributor.script.js;


import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import rhino.ClassShutter;


/**
 * This is a {@link ClassShutter} backed by 2 regexes. One act as the blacklist and the other the whitelist.
 */
public final class RegexClassShutter implements ClassShutter {

  private final Pattern blacklist;
  private final Pattern whitelist;

  public RegexClassShutter(@NotNull Iterable<String> blacklist, @NotNull Iterable<String> whitelist) {
    this.blacklist = Pattern.compile(String.join("|", blacklist));
    this.whitelist = Pattern.compile(String.join("|", whitelist));
  }

  /**
   * @return the blacklist regex
   */
  public @NotNull Pattern getBlacklist() {
    return blacklist;
  }

  /**
   * @return the whitelist regex
   */
  public @NotNull Pattern getWhitelist() {
    return whitelist;
  }

  @Override
  public boolean visibleToScripts(@NotNull String s) {
    return !(blacklist.matcher(s).matches() && !whitelist.matcher(s).matches());
  }
}
