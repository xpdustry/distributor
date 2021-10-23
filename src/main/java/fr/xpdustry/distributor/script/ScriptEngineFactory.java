package fr.xpdustry.distributor.script;

import org.mozilla.javascript.*;


public interface ScriptEngineFactory{
    ScriptEngine makeEngine();

    ScriptEngineFactory DEFAULT = () -> {
        Context context = Context.getCurrentContext();
        if(context == null) context = Context.enter();
        return new ScriptEngine(context);
    };
}
