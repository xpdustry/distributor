package fr.xpdustry.distributor.command.mindustry;

import arc.util.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class CommandContainer<C> extends MindustryCommand<C>{
    private final CommandParser parser;
    private final SortedMap<String, MindustryCommand<C>> subcommands = new TreeMap<>();

    public CommandContainer(String name, String parameterText, String description, List<CommandParameter<?>> parameters,
                            ContextRunner<C> responseHandler, CommandParser parser, TypeToken<? extends C> callerType){
        super(name, parameterText, description, parameters, responseHandler, callerType);
        this.parser = parser;
    }

    public CommandParser getParser(){
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
}
