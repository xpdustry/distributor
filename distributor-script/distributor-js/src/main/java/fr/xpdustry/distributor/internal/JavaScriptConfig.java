package fr.xpdustry.distributor.internal;

import java.util.List;
import org.aeonbits.owner.Accessible;

public interface JavaScriptConfig extends Accessible {

  @DefaultValue("")
  @Key("distributor.script.js.startup")
  List<String> getStartupScripts();

  @DefaultValue("")
  @Key("distributor.script.js.blacklist")
  List<String> getBlackList();

  @DefaultValue("")
  @Key("distributor.script.js.whitelist")
  List<String> getWhiteList();

  @DefaultValue("10")
  @Key("distributor.script.js.max-runtime")
  int getMaxScriptRuntime();
}
