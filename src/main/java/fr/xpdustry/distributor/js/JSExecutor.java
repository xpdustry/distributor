package fr.xpdustry.distributor.js;

import rhino.*;


public class JSExecutor{
    public final Context ctx;
    public final ImporterTopLevel importer;

    public JSExecutor(){
        this(Context.getCurrentContext());
    }

    public JSExecutor(Context context){
        this.ctx = context;
        this.importer = new ImporterTopLevel(ctx);
    }

    public Scriptable newScope(){
        return ctx.newObject(importer);
    }

    public String evaluate(Scriptable scope, String source, String sourceName){
        try{
            Object result = ctx.evaluateString(scope, source, sourceName, 1, null);
            if(result instanceof NativeJavaObject n) result = n.unwrap();
            if(result instanceof Undefined) result = "undefined";
            return String.valueOf(result);
        }catch(Throwable t){
            return t.getClass().getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
        }
    }
}
