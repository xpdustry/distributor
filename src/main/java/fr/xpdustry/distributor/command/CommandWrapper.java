package fr.xpdustry.distributor.command;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.Command;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.number.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


public class CommandWrapper implements CommandRunner<Playerc>{
    private final @NotNull Command<Playerc> command;

    public CommandWrapper(@NotNull Command<Playerc> command){
        this.command = requireNonNull(command, "command can't be null.");
    }

    @Override
    public void accept(@NotNull String[] args, @Nullable Playerc player){
        if(player == null){
            player = MindustryCaller.DUMMY_PLAYER;
        }

        CommandContext<Playerc> context = new CommandContext<>(player, Arrays.asList(args), command);
        MindustryCaller wrapper = new MindustryCaller(player);

        try{
            context.invoke();
        }catch(ArgumentSizeException e){
            wrapper.send(
                "The expected argument size for this command is at least @ or at most @, got @.",
                e.getMinArgumentSize(), e.getMaxArgumentSize(), e.getActualArgumentSize());
        }catch(ArgumentParsingException e){
            wrapper.send(
                "The argument '@' is invalid. The parameter '@' expects argument of type @.",
                e.getArgument(), e.getParameter().getName(), ToolBox.getSimpleTypeName(e.getParameter().getValueType()));
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                wrapper.send(
                    "The numeric parameter '@' only accepts arguments between @ and @, got @.",
                    p.getName(), p.getMin(), p.getMax(), e.getArgument());
            }else{
                wrapper.send(
                    "The argument '@' does not meet the requirements of the parameter '@'",
                    e.getArgument(), e.getParameter().getName());
            }
        }catch(ArgumentException e){
            wrapper.send("An unknown exception happened. Please report it to us.");
        }
    }

    public @NotNull Command<Playerc> getCommand(){
        return command;
    }
}
