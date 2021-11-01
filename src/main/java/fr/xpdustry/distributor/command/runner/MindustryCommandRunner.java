package fr.xpdustry.distributor.command.runner;

import arc.util.*;
import arc.util.CommandHandler.*;
import arc.util.Nullable;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.number.*;

import org.jetbrains.annotations.*;

import java.util.*;


public class MindustryCommandRunner implements CommandRunner<Playerc>{
    private final @NotNull MindustryCommand<Playerc> command;

    public MindustryCommandRunner(@NotNull MindustryCommand<Playerc> command){
        this.command = command;
    }

    @Override
    public void accept(@NotNull String[] args, @Nullable Playerc player){
        if(player == null) player = MindustryCaller.DUMMY_PLAYER;
        CommandContext<Playerc> context = new CommandContext<>(player, Arrays.asList(args), command);
        MindustryCaller wrapper = new MindustryCaller(player);

        try{
            context.invoke();
        }catch(ArgumentSizeException e){
            wrapper.err("The expected argument size for this command is at least @ or at most @, got @.",
                e.getMinArgumentSize(), e.getMaxArgumentSize(), e.getActualArgumentSize());
        }catch(ArgumentParsingException e){
            wrapper.err("The argument '@' is invalid. The parameter '@' expects argument of type @.",
                e.getArgument(), e.getParameter().getName(), ToolBox.getSimpleTypeName(e.getParameter().getValueType()));
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                wrapper.err("The numeric parameter '@' only accepts arguments between @ and @, got @.",
                    p.getName(), p.getMin(), p.getMax(), e.getArgument());
            }else{
                wrapper.err("The argument '@' does not meet the requirements of the parameter '@'",
                    e.getArgument(), e.getParameter().getName());
            }
        }catch(ArgumentException e){
            wrapper.err("An unknown exception happened.");
        }
    }
}
