package fr.xpdustry.distributor.script;

import fr.xpdustry.distributor.exception.*;

import org.mozilla.javascript.*;

import java.io.*;
import java.util.concurrent.atomic.*;


public final class JavaScriptEngine{
    private final Context ctx;
    private final ImporterTopLevel importer;

    private static final AtomicReference<ContextProvider> contextProvider =
        new AtomicReference<>(ContextProvider.DEFAULT);

    private static final ThreadLocal<JavaScriptEngine> instance =
        ThreadLocal.withInitial(() -> new JavaScriptEngine(contextProvider.get().getContext()));

    public static synchronized ContextProvider getGlobalContextProvider(){
        return JavaScriptEngine.contextProvider.get();
    }

    public static synchronized void setGlobalContextProvider(ContextProvider provider){
        JavaScriptEngine.contextProvider.set(provider);
    }

    public static JavaScriptEngine getInstance(){
        return instance.get();
    }

    private JavaScriptEngine(Context context){
        this.ctx = context;
        this.importer = new ImporterTopLevel(ctx);
    }

    public Scriptable newScope(){
        return newScope(importer);
    }

    public Scriptable newScope(Scriptable parent){
        Scriptable scope = ctx.newObject(parent);
        // Ensures that definitions in the root scope are found.
        scope.setPrototype(parent);
        // Ensures that new global variables are created in this scope (don't use var for them!)
        scope.setParentScope(null);
        return scope;
    }

    public Object eval(String source) throws ScriptException{
        return eval(importer, source, "engine.js");
    }

    public Object eval(Scriptable scope, String source, String sourceName) throws ScriptException{
        try{
            return ctx.evaluateString(scope, source, sourceName, 1, null);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Script compileScript(String source, String sourceName){
        return ctx.compileString(source, sourceName, 1, null);
    }

    public Script compileScript(Reader reader, String sourceName) throws IOException{
        return ctx.compileReader(reader, sourceName, 1, null);
    }

    public Function compileFunction(Scriptable scope, String source, String sourceName){
        return ctx.compileFunction(scope, source, sourceName, 1, null);
    }

    public Object invoke(Function function, Object... args) throws ScriptException{
        try{
            return function.call(ctx, importer, importer, args);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object invoke(Function function, Scriptable scope, Object... args) throws ScriptException{
        try{
            return function.call(ctx, scope, scope, args);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(Script script) throws ScriptException{
        try{
            return script.exec(ctx, importer);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(Script script, Scriptable scope) throws ScriptException{
        try{
            return script.exec(ctx, scope);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public interface ContextProvider{
        Context getContext();

        ContextProvider DEFAULT = () -> {
            Context context = Context.getCurrentContext();
            if(context == null) context = Context.enter();
            return context;
        };
    }
}
