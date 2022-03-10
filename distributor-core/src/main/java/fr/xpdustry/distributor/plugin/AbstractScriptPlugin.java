package fr.xpdustry.distributor.plugin;

import arc.util.Strings;
import cloud.commandframework.arguments.standard.StringArgument;
import fr.xpdustry.distributor.Distributor;
import fr.xpdustry.distributor.command.ArcCommandManager;
import fr.xpdustry.distributor.command.ArcMeta;
import fr.xpdustry.distributor.command.ArcPermission;
import fr.xpdustry.distributor.message.MessageIntent;
import java.util.List;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScriptPlugin extends AbstractPlugin implements ScriptEngineFactory {

  public abstract @NotNull String getEngineName();

  public abstract @NotNull String getEngineVersion();

  public abstract @NotNull List<String> getExtensions();

  public abstract @NotNull List<String> getMimeTypes();

  public abstract @NotNull List<String> getNames();

  public abstract @NotNull String getLanguageName();

  public abstract @NotNull String getLanguageVersion();

  public abstract @Nullable Object getParameter(final @NotNull String key);

  public abstract @NotNull String getMethodCallSyntax(final @NotNull String obj, final @NotNull String m, final @NotNull String... args);

  public abstract @NotNull String getOutputStatement(final @NotNull String toDisplay);

  public abstract @NotNull String getProgram(final @NotNull String... statements);

  public abstract @NotNull ScriptEngine getScriptEngine();

  @SuppressWarnings("NullAway") // <- ScriptException#getMessage() can't be null.
  @Override
  public void registerSharedCommands(final @NotNull ArcCommandManager manager) {
    manager.command(manager.commandBuilder(getExtensions().get(0))
      .meta(ArcMeta.DESCRIPTION, "Run arbitrary " + Strings.capitalize(getLanguageName()) + " code.")
      .meta(ArcMeta.PARAMETERS, "<script...>")
      .meta(ArcMeta.PLUGIN, asLoadedMod().name)
      .permission(ArcPermission.ADMIN.or(ArcPermission.SCRIPT))
      .argument(StringArgument.greedy("script"))
      .handler(ctx -> {
        try {
          final var output = getScriptEngine().eval(ctx.<String>get("script"));
          final var formatter = Distributor.getMessageFormatter(ctx.getSender());
          ctx.getSender().sendMessage(formatter.format(MessageIntent.NONE, String.valueOf(output)));
        } catch (ScriptException e) {
          ctx.getSender().sendMessage(e.getMessage());
        }
      })
    );
  }
}
