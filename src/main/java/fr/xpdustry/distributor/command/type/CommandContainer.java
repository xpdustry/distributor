package fr.xpdustry.distributor.command.type;

import arc.util.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class CommandContainer<C> extends MindustryCommand<C>{
    private final ParameterParser parser;
    private final SortedMap<String, MindustryCommand<C>> subcommands = new TreeMap<>();

    public CommandContainer(String name, String parameterText, String description,
                            ParameterParser parser, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType) throws ParsingException{
        super(name, parameterText, description, parser, responseHandler, callerType);
        this.parser = parser;
    }

    public CommandContainer(String name, String parameterText, String description,
                            ParameterParser parser, ContextRunner<C> responseHandler, Class<? extends C> callerType) throws ParsingException{
        this(name, parameterText, description, parser, responseHandler, TypeToken.get(callerType));
    }

    public ParameterParser getParser(){
        return parser;
    }

    @Nullable
    public MindustryCommand<C> getSubcommand(String name){
        return subcommands.get(name);
    }

    public boolean hasSubcommand(String name){
        return subcommands.containsKey(name);
    }

    public SortedMap<String, MindustryCommand<C>> getSubcommands(){
        return new TreeMap<>(subcommands);
    }

    protected MindustryCommand<C> addSubcommand(MindustryCommand<C> command){
        command.setParent(this);
        return subcommands.put(command.getName(), command);
    }

    @Nullable
    protected MindustryCommand<C> removeSubcommand(String name){
        return subcommands.remove(name);
    }

    protected boolean removeSubcommand(MindustryCommand<C> command){
        return subcommands.remove(command.getName(), command);
    }

    /*
    public CommandContext<C> makeContext(String[] args, C type){
        return new CommandContext<>(type, Arrays.asList(args));
    }
     */
}
