package fr.xpdustry.distributor.localization;


import org.jetbrains.annotations.*;

import java.util.*;


public class SimpleGlobalTranslator implements GlobalTranslator{
    private final Collection<Translator> translators = new HashSet<>();

    @Override public @NotNull Collection<Translator> getTranslators(){
        return Collections.unmodifiableCollection(translators);
    }

    @Override public void addTranslator(@NotNull Translator translator){
        translators.add(translator);
    }

    @Override public void removeTranslator(@NotNull Translator translator){
        translators.remove(translator);
    }
}
