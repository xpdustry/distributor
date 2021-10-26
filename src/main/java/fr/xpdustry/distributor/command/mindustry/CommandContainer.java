package fr.xpdustry.distributor.command.mindustry;

import arc.util.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class CommandContainer<C> extends MindustryCommand<C>{
    private final SortedMap<String, MindustryCommand<C>> subcommands = new TreeMap<>();

    public CommandContainer(String name, String description, List<CommandParameter<?>> parameters,
                            TypeToken<? extends C> callerType, ContextRunner<C> responseHandler){
        super(name, description, parameters, callerType, responseHandler);
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
