package fr.xpdustry.distributor.localization;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;

import java.util.*;


public interface Translator{
    static Locale getPlayerLocale(final @NotNull Player player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    static Translator empty(){
        return EmptyTranslator.getInstance();
    }

    @Nullable String translate(final @NotNull String key, final @NotNull Locale locale);

    default @Nullable String translate(final @NotNull Caption caption, final @NotNull Locale locale){
        return translate(caption.getKey(), locale);
    }
}
