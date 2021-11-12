package fr.xpdustry.distributor.command;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.Command;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.numeric.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static fr.xpdustry.distributor.command.Commands.getParameterTypeName;
import static java.util.Objects.requireNonNull;


public class CommandInvoker implements CommandRunner<Playerc>, ContextRunner<Playerc>{
    private final @NotNull Command<Playerc> command;
    private final @NotNull ContextFactory factory;

    public CommandInvoker(@NotNull Command<Playerc> command, @NotNull ContextFactory factory){
        this.command = requireNonNull(command, "command can't be null.");
        this.factory = requireNonNull(factory, "factory can't be null.");
    }

    public CommandInvoker(@NotNull Command<Playerc> command){
        this(command, ContextFactory.DEFAULT);
    }

    @Override
    public void accept(@NotNull String[] args, @Nullable Playerc player){
        if(player == null) player = Commands.SERVER_PLAYER;
        handleContext(factory.makeContext(player, List.of(args), command));
    }

    @Override
    public void handleContext(@NotNull CommandContext<Playerc> ctx){
        PlayerBundle bundle = Distributor.getBundleProvider().getBundle(ctx.getCaller());

        try{
            ctx.invoke();
        }catch(ArgumentSizeException e){
            if(e.getMaxArgumentSize() < e.getActualArgumentSize()){
                bundle.send("exc.command.arg.size.many", e.getMaxArgumentSize(), e.getActualArgumentSize());
            }else{
                bundle.send("exc.command.arg.size.few", e.getMinArgumentSize(), e.getActualArgumentSize());
            }
        }catch(ArgumentParsingException e){
            bundle.send("exc.command.arg.parsing", e.getParameter().getName(),
                getParameterTypeName(e.getParameter()), e.getArgument());
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                bundle.send("exc.command.arg.validation.numeric", p.getName(), p.getMin(), p.getMax(), e.getArgument());
            }else{
                bundle.send("exc.command.arg.validation", e.getParameter().getName(), e.getArgument());
            }
        }catch(ArgumentException e){
            bundle.send("exc.command.arg");
        }
    }

    public @NotNull Command<Playerc> getCommand(){
        return command;
    }

    @FunctionalInterface
    public interface ContextFactory{
        ContextFactory DEFAULT = CommandContext::new;

        @NotNull CommandContext<Playerc> makeContext(@NotNull Playerc player, @NotNull List<String> args,
                                                     @NotNull Command<Playerc> command, @Nullable CommandContext<Playerc> parent);

        default @NotNull CommandContext<Playerc> makeContext(@NotNull Playerc player, @NotNull List<String> args,
                                                             @NotNull Command<Playerc> command){
            return makeContext(player, args, command, null);
        }
    }
}
