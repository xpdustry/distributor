package fr.xpdustry.distributor.command.type;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.xcommand.context.*;

import java.util.*;


public class MindustryCommandRunner implements CommandRunner<Playerc>{
    private final MindustryCommand<Playerc> command;

    public MindustryCommandRunner(MindustryCommand<Playerc> command){
        this.command = command;
    }

    @Override
    public void accept(String[] args, Playerc caller){
        if(caller == null) caller = Player.create();
        CommandContext<Playerc> context = new CommandContext<>(caller, Arrays.asList(args), command);
        command.call(context);
    }

    public MindustryCommand<Playerc> getCommand(){
        return command;
    }
}
