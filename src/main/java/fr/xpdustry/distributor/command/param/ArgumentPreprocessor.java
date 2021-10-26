package fr.xpdustry.distributor.command.param;

import fr.xpdustry.distributor.exception.*;

import org.jetbrains.annotations.*;


public interface ArgumentPreprocessor<T>{
    T process(@NotNull String arg) throws ParsingException;
}
