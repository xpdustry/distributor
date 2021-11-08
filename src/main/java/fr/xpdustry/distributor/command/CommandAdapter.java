package fr.xpdustry.distributor.command;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.util.bundle.*;
import fr.xpdustry.xcommand.Command;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.numeric.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static fr.xpdustry.distributor.command.Commands.getParameterTypeName;
import static java.util.Objects.requireNonNull;


/**
 * This class wraps a {@code Command} for a {@code CommandHandler}.
 *
 * <blockquote>
 * <strong>Implementation notes</strong>
 * <br>
 * You should only subclass it if you add more parameter types.
 * </blockquote>
 */
public class CommandAdapter implements CommandRunner<Playerc>{
    private final @NotNull Command<Playerc> command;

    public CommandAdapter(@NotNull Command<Playerc> command){
        this.command = requireNonNull(command, "command can't be null.");
    }

    @Override
    public void accept(@NotNull String[] args, @Nullable Playerc player){
        if(player == null) player = Commands.SERVER_PLAYER;

        WrappedBundle bundle = WrappedBundle.from("bundles/bundle", player);
        CommandContext<Playerc> context = new CommandContext<>(player, List.of(args), command);

        try{
            context.invoke();
        }catch(ArgumentSizeException e){
            if(e.getMaxArgumentSize() < e.getActualArgumentSize()){
                bundle.send(player, "exc.command.arg.size.many", e.getMaxArgumentSize(), e.getActualArgumentSize());
            }else{
                bundle.send(player, "exc.command.arg.size.few", e.getMinArgumentSize(), e.getActualArgumentSize());
            }
        }catch(ArgumentParsingException e){
            bundle.send(player, "exc.command.arg.parsing", e.getParameter().getName(), getParameterTypeName(e.getParameter()), e.getArgument());
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                bundle.send(player, "exc.command.arg.validation.numeric", p.getName(), p.getMin(), p.getMax(), e.getArgument());
            }else{
                bundle.send(player, "exc.command.arg.validation", e.getParameter().getName(), e.getArgument());
            }
        }catch(ArgumentException e){
            bundle.send(player, "exc.command.arg");
        }
    }

    /** @return the wrapped command */
    public @NotNull Command<Playerc> getCommand(){
        return command;
    }
}
