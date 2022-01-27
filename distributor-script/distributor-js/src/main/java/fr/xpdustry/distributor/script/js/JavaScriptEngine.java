package fr.xpdustry.distributor.script.js;

import arc.files.*;

import fr.xpdustry.distributor.exception.*;

import org.checkerframework.checker.nullness.qual.*;
import rhino.Function;
import rhino.*;
import rhino.module.*;

import java.io.*;
import java.nio.charset.*;
import java.util.function.*;


public class JavaScriptEngine implements AutoCloseable{
    private static Supplier<JavaScriptEngine> factory = () -> {
        var ctx = Context.getCurrentContext();
        if(ctx == null) ctx = Context.enter();
        return new JavaScriptEngine(ctx);
    };

    private static final ThreadLocal<JavaScriptEngine> INSTANCE =
        ThreadLocal.withInitial(() -> JavaScriptEngine.factory.get());

    private final Context ctx;
    private final Scriptable scope;
    private @Nullable Require require = null;

    public JavaScriptEngine(@NonNull Context context, @NonNull Scriptable scope){
        this.ctx = context;
        this.scope = scope;
    }

    public JavaScriptEngine(@NonNull Context context){
        this(context, new ImporterTopLevel(context));
    }

    public static @NonNull Supplier<JavaScriptEngine> getGlobalFactory(){
        return factory;
    }

    public static void setGlobalFactory(@NonNull Supplier<JavaScriptEngine> factory){
        JavaScriptEngine.factory = factory;
    }

    /** @return the current or a new instance of {@code ScriptEngine} */
    public static JavaScriptEngine getInstance(){
        return INSTANCE.get();
    }

    /**
     * Extract the toString value of a javaScript object.
     *
     * @param obj the output object of a javascript function/script/evaluation.
     * @return a string representation of the object
     */
    public static String toString(@Nullable Object obj){
        if(obj instanceof NativeJavaObject n) obj = n.unwrap();
        if(obj instanceof Undefined) obj = "undefined";
        return String.valueOf(obj);
    }

    public @NonNull Scriptable newScope(){
        return newScope(scope);
    }

    public @NonNull Scriptable newScope(@NonNull Scriptable parent){
        final var scope = ctx.newObject(parent);
        // Ensures that definitions in the root scope are found.
        scope.setPrototype(parent);
        // Ensures that new global variables are created in this scope (don't use var for them!)
        scope.setParentScope(null);
        return scope;
    }

    public void setupRequire(@NonNull ModuleScriptProvider provider){
        require = new RequireBuilder()
            .setSandboxed(false)
            .setModuleScriptProvider(provider)
            .createRequire(ctx, scope);
        require.install(scope);
    }

    public final boolean hasRequire(){
        return require != null;
    }

    public @Nullable Require getRequire(){
        return require;
    }

    public Object eval(@NonNull String source) throws ScriptException{
        return eval(scope, source, toString());
    }

    public Object eval(@NonNull Scriptable scope, @NonNull String source, @NonNull String sourceName) throws ScriptException{
        try{
            return ctx.evaluateString(scope, source, sourceName, 1);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Script compileScript(@NonNull String source, @NonNull String sourceName){
        return ctx.compileString(source, sourceName, 1);
    }

    public Script compileScript(@NonNull Reader reader, @NonNull String sourceName) throws IOException{
        return ctx.compileReader(reader, sourceName, 1);
    }

    public Function compileFunction(@NonNull Scriptable scope, @NonNull String source, @NonNull String sourceName){
        return ctx.compileFunction(scope, source, sourceName, 1);
    }

    public Object invoke(@NonNull Function function, Object... args) throws ScriptException{
        return invoke(function, scope, args);
    }

    public Object invoke(@NonNull Function function, @NonNull Scriptable scope, Object... args) throws ScriptException{
        try{
            return function.call(ctx, scope, scope, args);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(@NonNull Script script) throws ScriptException{
        return exec(script, scope);
    }

    public Object exec(@NonNull Script script, @NonNull Scriptable scope) throws ScriptException{
        try{
            return script.exec(ctx, scope);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(@NonNull Fi file) throws IOException, ScriptException{
        return exec(file.file());
    }

    public Object exec(@NonNull File file) throws IOException, ScriptException{
        try(final var reader = new FileReader(file, StandardCharsets.UTF_8)){
            return exec(compileScript(reader, file.getName()));
        }
    }

    public @NonNull Context getContext(){
        return ctx;
    }

    public @NonNull Scriptable getScope(){
        return scope;
    }

    @Override public @NonNull String toString(){
        return "engine@" + Integer.toHexString(hashCode()) + ".js";
    }

    @Override public void close(){
        Context.exit();
        INSTANCE.remove();
    }
}
