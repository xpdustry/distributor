package fr.xpdustry.distributor.command;

import cloud.commandframework.arguments.parser.*;
import fr.xpdustry.distributor.command.argument.TeamArgument.*;
import io.leangen.geantyref.*;

public final class ArcParserParameters {

  public static final ParserParameter<TeamMode> TEAM_MODE = new ParserParameter<>("team_mode", TypeToken.get(TeamMode.class));

  private ArcParserParameters() {
  }
}
