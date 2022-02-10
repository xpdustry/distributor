package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;


public class ArcServerSender extends ArcCommandSender{
    public ArcServerSender(final @NonNull Translator translator, final @NonNull MessageFormatter formatter){
        super(translator, formatter);
    }

    public ArcServerSender(final @NonNull Translator translator){
        super(translator, new ServerMessageFormatter());
    }

    public ArcServerSender(){
        super();
    }

    @Override public boolean isPlayer(){
        return false;
    }

    @Override public @NonNull Player asPlayer(){
        throw new UnsupportedOperationException("Cannot convert console to player");
    }

    /** @return the {@link Locale#getDefault() default locale} of the system. */
    @Override public @NonNull Locale getLocale(){
        return Locale.getDefault();
    }

    /** @return always true */
    @Override public boolean hasPermission(final @NonNull String permission){
        return true;
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        getLogger(intent).accept(getFormatter().format(intent, message, args));
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull String message, @NonNull CaptionVariable... vars){
        getLogger(intent).accept(getFormatter().format(intent, message, vars));
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        final var translation = getTranslator().translate(caption, getLocale());
        getLogger(intent).accept(getFormatter().format(intent, translation == null ? "???" + caption.getKey() + "???" : translation, vars));
    }

    protected Consumer<String> getLogger(@NonNull MessageIntent intent){
        return switch(intent){
            case DEBUG -> Log::debug; case ERROR -> Log::err; default -> Log::info;
        };
    }

    /** This formatter performs the formatting of a default mindustry server where arguments are colored. */
    public static class ServerMessageFormatter implements ColoringMessageFormatter{
        @Override public @NonNull String prefix(final @NonNull MessageIntent intent){
            return "";
        }

        @Override public @NonNull String argument(final @NonNull MessageIntent intent, final @NonNull String arg){
            return "&fb&lb" + arg + "&fr";
        }
    }
}
