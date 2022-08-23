package fr.xpdustry.distributor;

import arc.util.*;
import fr.xpdustry.distributor.permission.*;
import fr.xpdustry.distributor.audience.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.translate.*;
import java.util.*;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class DistributorPlugin extends ExtendedPlugin {

  public static final String NAMESPACE = "xpdustry-distributor";

  private static Translator translator;
  private static PermissionChecker permissions = PermissionChecker.admin();
  private static AudienceProvider audiences = new SimpleAudienceProvider();

  static {
    final var translator = new SimpleDelegatingTranslator();
    translator.registerTranslator(Translator.router());
    translator.registerTranslator(Translator.bundle("bundle/bundles", DistributorPlugin.class.getClassLoader()));
    DistributorPlugin.translator = translator;
  }

  private final ArcCommandManager<Audience> clientCommands = ArcCommandManager.audience(this);
  private final ArcCommandManager<Audience> serverCommands = ArcCommandManager.audience(this);

  // TODO Fix the null checks in the static getters and setters
  public static Translator getGlobalTranslator() {
    return translator;
  }

  public static void setGlobalTranslator(final Translator translator) {
    DistributorPlugin.translator = translator;
  }

  public static PermissionChecker getPermissionManager() {
    return Objects.requireNonNull(permissions);
  }

  public static void setPermissionManager(final PermissionChecker permissions) {
    DistributorPlugin.permissions = permissions;
  }

  public static AudienceProvider getAudienceProvider() {
    return Objects.requireNonNull(audiences);
  }

  public static void setAudienceProvider(final AudienceProvider audiences) {
    DistributorPlugin.audiences = audiences;
  }

  @Override
  public void onClientCommandsRegistration(CommandHandler handler) {
    clientCommands.initialize(handler);
  }

  @Override
  public void onServerCommandsRegistration(CommandHandler handler) {
    serverCommands.initialize(handler);
  }
}
