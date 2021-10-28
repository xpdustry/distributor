package fr.xpdustry.distributor.script;

import org.mozilla.javascript.*;


public interface ScriptEngineFactory{
    ScriptEngineFactory DEFAULT = () -> {
        Context context = Context.getCurrentContext();
        if(context == null) context = Context.enter();
        return new ScriptEngine(context);
    };

    ScriptEngine makeEngine();
}
