package fr.xpdustry.distributor.command.ext;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.js.*;

import rhino.*;


public class JSCommand<T> extends Command<T>{
    private Scriptable scope;
    /** The {@code this} of js */
    private Scriptable that;
    private Function function;
    private final JSExecutor engine;

    @SuppressWarnings("unchecked")
    public JSCommand(String name, String parameterText, String description, JSExecutor engine, Scriptable scope, Scriptable that, Function function){
        super(name, parameterText, description, (CommandRunner<T>)CommandRunner.voidRunner);

        this.scope = scope;
        this.that = that;
        this.engine = engine;

        this.runner = (args, type) -> {
            this.function.call(this.engine.ctx, this.scope, this.that, new Object[]{args, type});
        };
    }
}
