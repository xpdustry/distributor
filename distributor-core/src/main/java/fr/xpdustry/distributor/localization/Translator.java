package fr.xpdustry.distributor.localization;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface Translator{
    static Locale getPlayerLocale(final @NonNull Player player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    static Translator empty(){
        return EmptyTranslator.getInstance();
    }

    @Nullable String translate(final @NonNull String key, final @NonNull Locale locale);

    default @Nullable String translate(final @NonNull Caption caption, final @NonNull Locale locale){
        return translate(caption.getKey(), locale);
    }
}
