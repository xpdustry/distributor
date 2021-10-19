package fr.xpdustry.distributor.command.type;

import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.exception.*;

import java.util.*;


public interface ParameterParser{
    List<CommandParameter<?>> parseParameters(String parameterText) throws ParsingException;
}
