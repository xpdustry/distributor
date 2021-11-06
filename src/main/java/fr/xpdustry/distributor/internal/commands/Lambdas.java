package fr.xpdustry.distributor.internal.commands;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.parameter.string.*;

import org.mozilla.javascript.*;

import java.util.*;

import static fr.xpdustry.distributor.command.Commands.DEFAULT_ADMIN_VALIDATOR;


public final class Lambdas{
    public static final LambdaCommand<Playerc> jscriptCommand =
        LambdaCommand.of("jscript", Commands.PLAYER_TYPE)
            .validator(DEFAULT_ADMIN_VALIDATOR)
            .description("Run some random js code.")
            .parameter(StringParameter.of("script").variadic().tokenizer(Collections::singletonList))
            .runner(ctx -> {
                List<String> script = ctx.get("script");
                try{
                    Object obj = ScriptEngine.getInstance().eval(script.get(0));
                    ctx.getCaller().sendMessage(">>> " + ToolBox.scriptObjectToString(obj));
                    ctx.setResult(obj);
                }catch(ScriptException e){
                    ctx.getCaller().sendMessage(e.getMessage());
                    ctx.setResult(Undefined.instance);
                }
            }).build();

    private Lambdas(){
        /* Don't mind me, I am doing some lambda commands... */
    }
}
