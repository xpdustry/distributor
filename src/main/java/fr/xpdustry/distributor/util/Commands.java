package fr.xpdustry.distributor.util;

import arc.util.*;
import arc.util.CommandHandler.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.type.*;

import java.util.*;


public final class Commands{
    private Commands(){
        /* Sus... */
    }

    public static <C> MindustryCommandRunner<C> register(CommandHandler handler, MindustryCommand<C> command){
        MindustryCommandRunner<C> runner = new MindustryCommandRunner<>(command);
        handler.register(command.getName(), command.getParameterText(), command.getDescription(), runner);
        return runner;
    }

    public static class MindustryCommandRunner<C> implements CommandRunner<C>{
        private final MindustryCommand<C> command;

        public MindustryCommandRunner(MindustryCommand<C> command){
            this.command = command;
        }

        @Override
        public void accept(String[] args, C type){
            CommandContext<C> context = new CommandContext<>(type, Arrays.asList(args));
            command.call(context);
        }
    }
}
