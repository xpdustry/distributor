package fr.xpdustry.distributor.script.js;

import arc.files.*;

import fr.xpdustry.distributor.exception.*;

import org.checkerframework.checker.nullness.qual.*;
import rhino.Function;
import rhino.*;
import rhino.module.*;
import rhino.module.provider.*;

import java.io.*;
import java.nio.charset.*;
import java.util.function.*;


public class JavaScriptEngine implements AutoCloseable{
    private static @NonNull Supplier<JavaScriptEngine> factory = () -> {
        Context context = Context.getCurrentContext();
        if(context == null) context = Context.enter();
        return new JavaScriptEngine(context);
    };

    private static final ThreadLocal<JavaScriptEngine> instance =
        ThreadLocal.withInitial(() -> JavaScriptEngine.factory.get());

    private final @NonNull Context ctx;
    private final @NonNull Scriptable scope;
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
        return instance.get();
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
        Scriptable scope = ctx.newObject(parent);
        // Ensures that definitions in the root scope are found.
        scope.setPrototype(parent);
        // Ensures that new global variables are created in this scope (don't use var for them!)
        scope.setParentScope(null);
        return scope;
    }

    public void setupRequire(@NonNull ClassLoader loader){
        if(hasRequire()) throw new IllegalStateException("The require is already set up for this engine");

        require = new RequireBuilder()
            .setSandboxed(false)
            .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new JavaScriptLoader(loader)))
            .createRequire(ctx, scope);

        require.install(scope);
    }

    public final boolean hasRequire(){
        return require != null;
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
        try(InputStream stream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)){
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

    @Override
    public void close(){
        Context.exit();
        instance.remove();
    }
}
