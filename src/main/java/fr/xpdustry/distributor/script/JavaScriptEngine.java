package fr.xpdustry.distributor.script;

import org.mozilla.javascript.*;

import java.io.*;


public final class JavaScriptEngine{
    private final Context ctx;
    private final ImporterTopLevel importer;

    private static ContextProvider provider = ContextProvider.DEFAULT;
    private static final ThreadLocal<JavaScriptEngine> instance = ThreadLocal.withInitial(() -> new JavaScriptEngine(provider.getContext()));

    public static synchronized void setGlobalContextProvider(ContextProvider provider){
        JavaScriptEngine.provider = provider;
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

    public String evaluate(String source){
        return evaluate(importer, source, "engine.js");
    }

    public String evaluate(Scriptable scope, String source, String sourceName){
        try{
            Object result = ctx.evaluateString(scope, source, sourceName, 1, null);
            if(result instanceof NativeJavaObject) result = ((NativeJavaObject)result).unwrap();
            if(result instanceof Undefined) result = "undefined";
            return String.valueOf(result);
        }catch(Exception e){
            return e.getClass().getSimpleName() + (e.getMessage() == null ? "" : ": " + e.getMessage());
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

    public Object invoke(Function function, Object... args){
        return function.call(ctx, importer, importer, args);
    }

    public Object invoke(Function function, Scriptable scope, Object... args){
        return function.call(ctx, scope, scope, args);
    }

    public Object exec(Script script){
        return script.exec(ctx, importer);
    }

    public Object exec(Script script, Scriptable scope){
        return script.exec(ctx, scope);
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
