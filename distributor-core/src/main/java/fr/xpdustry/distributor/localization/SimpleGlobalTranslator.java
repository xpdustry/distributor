package fr.xpdustry.distributor.localization;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class SimpleGlobalTranslator implements GlobalTranslator{
    private final Collection<Translator> translators = new HashSet<>();

    @Override public @NonNull Collection<Translator> getTranslators(){
        return Collections.unmodifiableCollection(translators);
    }

    @Override public void addTranslator(@NonNull Translator translator){
        translators.add(translator);
    }

    @Override public void removeTranslator(@NonNull Translator translator){
        translators.remove(translator);
    }
}
