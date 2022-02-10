package fr.xpdustry.distributor.command.sender;

import arc.util.*;
import arc.util.Nullable;

import mindustry.gen.*;

import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


import java.util.*;
import java.util.function.*;


public class ArcServerSender extends ArcCommandSender{
    public ArcServerSender(final @NotNull Translator translator, final @NotNull MessageFormatter formatter){
        super(translator, formatter);
    }

    public ArcServerSender(final @NotNull Translator translator){
        super(translator, new ServerMessageFormatter());
    }

    public ArcServerSender(){
        super();
    }

    @Override public boolean isPlayer(){
        return false;
    }

    @Override public @NotNull Player asPlayer(){
        throw new UnsupportedOperationException("Cannot convert console to player");
    }

    /** @return the {@link Locale#getDefault() default locale} of the system. */
    @Override public @NotNull Locale getLocale(){
        return Locale.getDefault();
    }

    /** @return always true */
    @Override public boolean hasPermission(final @NotNull String permission){
        return true;
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args){
        getLogger(intent).accept(getFormatter().format(intent, message, args));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars){
        getLogger(intent).accept(getFormatter().format(intent, message, vars));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull Caption caption, @NotNull CaptionVariable... vars){
        final var translation = getTranslator().translate(caption, getLocale());
        getLogger(intent).accept(getFormatter().format(intent, translation == null ? "???" + caption.getKey() + "???" : translation, vars));
    }

    protected Consumer<String> getLogger(@NotNull MessageIntent intent){
        return switch(intent){
            case DEBUG -> Log::debug; case ERROR -> Log::err; default -> Log::info;
        };
    }

    /** This formatter performs the formatting of a default mindustry server where arguments are colored. */
    public static class ServerMessageFormatter implements ColoringMessageFormatter{
        @Override public @NotNull String prefix(final @NotNull MessageIntent intent){
            return "";
        }

        @Override public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg){
            return "&fb&lb" + arg + "&fr";
        }
    }
}
