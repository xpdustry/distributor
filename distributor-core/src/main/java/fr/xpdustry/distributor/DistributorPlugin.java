package fr.xpdustry.distributor;

import arc.util.*;
import fr.xpdustry.distributor.admin.*;
import fr.xpdustry.distributor.audience.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.format.*;
import fr.xpdustry.distributor.translate.*;
import fr.xpdustry.distributor.ui.*;
import fr.xpdustry.distributor.ui.MenuInterface.*;
import java.io.*;
import java.util.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;

// https://api.github.com/repos/Anuken/Mindustry/contents/core/assets/bundles
@SuppressWarnings({"NotNullFieldNotInitialized", "NullAway"})
public final class DistributorPlugin extends ExtendedPlugin {

  public static final String NAMESPACE = "xpdustry-distributor";

  private static Translator translator;
  private static PermissionManager permissions;
  private static AudienceProvider audiences;

  static {
    final var translator = new SimpleDelegatingTranslator();
    translator.registerTranslator(Translator.router());
    translator.registerTranslator(Translator.bundle("bundle/bundles", DistributorPlugin.class.getClassLoader()));
    DistributorPlugin.translator = translator;
  }

  private final ArcCommandManager<Audience> clientCommands = ArcCommandManager.audience(this);
  private final ArcCommandManager<Audience> serverCommands = ArcCommandManager.audience(this);

  public static Translator getGlobalTranslator() {
    return translator;
  }

  public static void setGlobalTranslator(Translator translator) {
    DistributorPlugin.translator = translator;
  }

  public static PermissionManager getPermissionManager() {
    return Objects.requireNonNull(permissions);
  }

  public static void setPermissionManager(final PermissionManager permissions) {
    DistributorPlugin.permissions = permissions;
  }

  public static AudienceProvider getAudienceProvider() {
    return Objects.requireNonNull(audiences);
  }

  public static void setAudienceProvider(final AudienceProvider audiences) {
    DistributorPlugin.audiences = audiences;
  }

  @Override
  public void onInit() {
    translator = new SimpleDelegatingTranslator();
    permissions = new SimplePermissionManager(getDirectory().toPath(), "permissions");
    audiences = new SimpleAudienceProvider();
  }

  @Override
  public void onLoad() {
    try {
      getPermissionManager().load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onClientCommandsRegistration(CommandHandler handler) {
    final var menu = new MenuInterface(
      InterfaceAction.nothing(),
      (player, metadata) -> {
        final int presses = metadata.getMetadata("presses", Integer.class).orElse(0);
        return MenuView.builder()
          .withTitle(Components.text("Hello random player :)"))
          .withContent(Components.text("What the fuck ?", TextColor.RED))
          .addRow(
            new Option(Components.text("Yes"), InterfaceAction.reopen()),
            new Option(Components.text("No"), InterfaceAction.nothing())
          )
          .addRow(
            new Option(Components.text(Integer.toString(presses)), (p, i) -> {
              i.show(
                player,
                MetadataContainer.builder()
                  .withConstant("presses", presses + 1)
                  .build()
              );
            })
          )
          .build();
      }
    );

    Config

    handler.<Player>register("test", "show me", (args, player) -> {
      menu.show(player);
    });

    clientCommands.initialize(handler);
    try {
      clientCommands.createAnnotationParser(Audience.class).parseContainers();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onServerCommandsRegistration(CommandHandler handler) {
    serverCommands.initialize(handler);
    try {
      serverCommands.createAnnotationParser(Audience.class).parseContainers();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getNamespace() {
    return NAMESPACE;
  }
}
