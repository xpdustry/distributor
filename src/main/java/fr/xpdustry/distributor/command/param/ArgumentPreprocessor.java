package fr.xpdustry.distributor.command.param;

import fr.xpdustry.distributor.exception.*;

public interface ArgumentPreprocessor<T>{
    T process(String arg) throws ParsingException;
}
