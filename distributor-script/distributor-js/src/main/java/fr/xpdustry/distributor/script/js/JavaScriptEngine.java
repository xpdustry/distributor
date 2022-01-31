package fr.xpdustry.distributor.script.js;

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

    private final Context context;
    private final Scriptable globalScope;
    private @Nullable Require require = null;

    public JavaScriptEngine(final @NonNull Context context, final @NonNull Scriptable globalScope){
        this.context = context;
        this.globalScope = globalScope;
    }

    public JavaScriptEngine(final @NonNull Context context){
        this(context, new ImporterTopLevel(context));
    }

    public static Supplier<JavaScriptEngine> getGlobalFactory(){
        return factory;
    }

    public static void setGlobalFactory(final @NonNull Supplier<@NonNull JavaScriptEngine> factory){
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
        return newScope(globalScope);
    }

    public @NonNull Scriptable newScope(final @NonNull Scriptable parent){
        final var scope = context.newObject(parent);
        // Ensures that definitions in the root scope are found.
        scope.setPrototype(parent);
        // Ensures that new global variables are created in this scope (don't use var for them!)
        scope.setParentScope(null);
        return scope;
    }

    public void setupRequire(final @NonNull ModuleScriptProvider provider){
        require = new RequireBuilder()
            .setSandboxed(false)
            .setModuleScriptProvider(provider)
            .createRequire(context, globalScope);
        require.install(globalScope);
    }

    public final boolean hasRequire(){
        return require != null;
    }

    public @Nullable Require getRequire(){
        return require;
    }

    public @Nullable Object eval(
        final @NonNull Scriptable scope,
        final @NonNull String source,
        final @NonNull String sourceName
    ) throws ScriptException{
        try{
            return context.evaluateString(scope, source, sourceName, 1);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public @Nullable Object eval(final @NonNull String source) throws ScriptException{
        return eval(globalScope, source, toString());
    }

    public @NonNull Script compileScript(final @NonNull String source, final @NonNull String sourceName){
        return context.compileString(source, sourceName, 1);
    }

    public @NonNull Script compileScript(final @NonNull Reader reader, final @NonNull String sourceName) throws IOException{
        return context.compileReader(reader, sourceName, 1);
    }

    public @NonNull Function compileFunction(
        final @NonNull Scriptable scope,
        final @NonNull String source,
        final @NonNull String sourceName
    ){
        return context.compileFunction(scope, source, sourceName, 1);
    }

    public @Nullable Object invoke(final @NonNull Function function, final @Nullable Object... args) throws ScriptException{
        return invoke(function, globalScope, args);
    }

    public @Nullable Object invoke(
        final @NonNull Function function,
        final @NonNull Scriptable scope,
        final @Nullable Object... args
    ) throws ScriptException{
        try{
            return function.call(context, scope, scope, args);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public @Nullable Object exec(final @NonNull Script script, final @NonNull Scriptable scope) throws ScriptException{
        try{
            return script.exec(context, scope);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public @Nullable Object exec(final @NonNull Script script) throws ScriptException{
        return exec(script, globalScope);
    }

    public @Nullable Object exec(final @NonNull File file, final @NonNull Scriptable scope) throws IOException, ScriptException{
        try(final var reader = new FileReader(file, StandardCharsets.UTF_8)){
            return exec(compileScript(reader, file.getName()), scope);
        }
    }

    public @Nullable Object exec(final @NonNull File file) throws IOException, ScriptException{
        return exec(file, globalScope);
    }

    public @NonNull Context getContext(){
        return context;
    }

    public @NonNull Scriptable getGlobalScope(){
        return globalScope;
    }

    @Override public @NonNull String toString(){
        return "engine@" + Integer.toHexString(hashCode()) + ".js";
    }

    @Override public void close(){
        Context.exit();
        INSTANCE.remove();
    }
}
