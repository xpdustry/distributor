package fr.xpdustry.distributor.internal.commands;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.parameter.*;
import fr.xpdustry.xcommand.parameter.string.*;

import org.mozilla.javascript.*;


public final class Lambdas{
    public static final Command<Playerc> jsx = Commands.builder("jsx")
        .validator(Commands.ADMIN_VALIDATOR)
        .description("Run some javascript with Distributor.")
        .parameter(StringParameter.of("script").variadic().tokenizer(ParameterTokenizer.NONE))
        .runner(ctx -> {
            try{
                var obj = ScriptEngine.getInstance().eval(ctx.getArgument(0));
                ctx.getCaller().sendMessage(">>> " + ScriptEngine.toString(obj));
                ctx.setResult(obj);
            }catch(ScriptException e){
                ctx.getCaller().sendMessage(e.getMessage());
                ctx.setResult(Undefined.SCRIPTABLE_UNDEFINED);
            }
        }).build();
}
