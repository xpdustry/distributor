package fr.xpdustry.distributor.command;

import arc.util.*;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class CommandContainer<C> extends MindustryCommand<C>{
    private final SortedMap<String, MindustryCommand<C>> subcommands = new TreeMap<>();

    public CommandContainer(String name, String description, List<CommandParameter<?>> parameters,
                            TypeToken<? extends C> callerType, CallerValidator<C> callerValidator){
        super(name, description, parameters, callerType, callerValidator);
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
