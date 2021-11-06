package fr.xpdustry.distributor.command;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.Command;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.numeric.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static fr.xpdustry.distributor.util.ToolBox.getSimpleTypeName;
import static java.util.Objects.requireNonNull;


public class CommandWrapper implements CommandRunner<Playerc>{
    private final @NotNull Command<Playerc> command;

    public CommandWrapper(@NotNull Command<Playerc> command){
        this.command = requireNonNull(command, "command can't be null.");
    }

    @Override
    public void accept(@NotNull String[] args, @Nullable Playerc player){
        if(player == null){
            player = Commands.SERVER_PLAYER;
        }

        WrappedBundle bundle = WrappedBundle.from("bundles/bundle", ToolBox.getLocale(player));
        CommandContext<Playerc> context = new CommandContext<>(player, List.of(args), command);

        try{
            context.invoke();
        }catch(ArgumentSizeException e){
            if(e.getMaxArgumentSize() < e.getActualArgumentSize()){
                player.sendMessage(bundle.get("ex.command.arg.size.many", e.getMaxArgumentSize(), e.getActualArgumentSize()));
            }else{
                player.sendMessage(bundle.get("ex.command.arg.size.few", e.getMinArgumentSize(), e.getActualArgumentSize()));
            }
        }catch(ArgumentParsingException e){
            player.sendMessage(bundle.get("ex.command.arg.parsing", e.getParameter().getName(),
                getSimpleTypeName(e.getParameter().getValueType()), e.getArgument()));
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                player.sendMessage(bundle.get("ex.command.arg.validation.int", p.getName(), p.getMin(), p.getMax(), e.getArgument()));
            }else{
                player.sendMessage(bundle.get("ex.command.arg.validation", e.getParameter().getName(), e.getArgument()));
            }
        }catch(ArgumentException e){
            player.sendMessage(bundle.get("ex.command.arg"));
        }
    }

    public @NotNull Command<Playerc> getCommand(){
        return command;
    }
}
