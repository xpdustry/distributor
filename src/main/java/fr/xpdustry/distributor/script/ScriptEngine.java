package fr.xpdustry.distributor.script;

import fr.xpdustry.distributor.exception.*;

import org.mozilla.javascript.*;

import java.io.*;
import java.util.concurrent.atomic.*;


public final class ScriptEngine{
    private static final AtomicReference<ScriptEngineFactory> factory =
        new AtomicReference<>(ScriptEngineFactory.DEFAULT);

    private static final ThreadLocal<ScriptEngine> threadInstance =
        ThreadLocal.withInitial(() -> factory.get().makeEngine());

    private final Context ctx;
    private final ImporterTopLevel importer;

    public ScriptEngine(Context context){
        this.ctx = context;
        this.importer = new ImporterTopLevel(ctx);
    }

    public static ScriptEngineFactory getGlobalFactory(){
        return factory.get();
    }

    public static void setGlobalFactory(ScriptEngineFactory factory){
        ScriptEngine.factory.set(factory);
    }

    public static ScriptEngine getInstance(){
        return threadInstance.get();
    }

    public static String toString(Object obj){
        if(obj instanceof NativeJavaObject) obj = ((NativeJavaObject)obj).unwrap();
        if(obj instanceof Undefined) obj = "undefined";
        return String.valueOf(obj);
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
        return eval(importer, source, "engine@" + Integer.toHexString(hashCode()) + ".js");
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

    public Context getContext(){
        return ctx;
    }

    public ImporterTopLevel getImporter(){
        return importer;
    }
}
