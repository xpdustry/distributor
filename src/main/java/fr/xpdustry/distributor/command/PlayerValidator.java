package fr.xpdustry.distributor.command;

import mindustry.gen.*;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;

import org.jetbrains.annotations.*;

import java.util.function.*;

import static java.util.Objects.requireNonNull;


/**
 * A handy validator for players.
 */
public class PlayerValidator implements ContextValidator<Playerc>{
    private final @NotNull Predicate<Playerc> predicate;
    private final @NotNull Consumer<Playerc> fail;

    public PlayerValidator(@NotNull Predicate<Playerc> predicate, @NotNull Consumer<Playerc> fail){
        this.predicate = requireNonNull(predicate, "predicate can't be null.");
        this.fail = requireNonNull(fail, "fail can't be null.");
    }

    @Override
    public boolean isValid(@NotNull CommandContext<Playerc> ctx){
        if(predicate.test(ctx.getCaller())){
            return true;
        }else{
            fail.accept(ctx.getCaller());
            return false;
        }
    }
}
