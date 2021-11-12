package fr.xpdustry.distributor.script.js;

import arc.files.*;

import fr.xpdustry.distributor.exception.*;

import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import java.io.*;
import java.util.concurrent.atomic.*;


public class JavaScriptEngine implements AutoCloseable{
    private static final AtomicReference<ScriptEngineFactory> factory =
        new AtomicReference<>(ScriptEngineFactory.DEFAULT);

    private static final ThreadLocal<JavaScriptEngine> threadInstance =
        ThreadLocal.withInitial(() -> factory.get().makeEngine());

    private final @NotNull Context ctx;
    private final ImporterTopLevel importer;
    private @Nullable Require require = null;

    public JavaScriptEngine(@NotNull Context context){
        this.ctx = context;
        this.importer = new ImporterTopLevel(ctx);
    }

    public static ScriptEngineFactory getGlobalFactory(){
        return factory.get();
    }

    public static void setGlobalFactory(ScriptEngineFactory factory){
        JavaScriptEngine.factory.set(factory);
    }

    /** @return the current or a new instance of {@code ScriptEngine} */
    public static JavaScriptEngine getInstance(){
        return threadInstance.get();
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

    public @NotNull Scriptable newScope(){
        return newScope(importer);
    }

    public @NotNull Scriptable newScope(@NotNull Scriptable parent){
        Scriptable scope = ctx.newObject(parent);
        // Ensures that definitions in the root scope are found.
        scope.setPrototype(parent);
        // Ensures that new global variables are created in this scope (don't use var for them!)
        scope.setParentScope(null);
        return scope;
    }

    public void setupRequire(@NotNull ClassLoader loader){
        if(hasRequire()) throw new IllegalStateException("The require is already set up for this engine");

        require = new RequireBuilder()
            .setSandboxed(false)
            .setModuleScriptProvider(
                new SoftCachingModuleScriptProvider(new JavaScriptLoader(loader)))
            .createRequire(ctx, importer);

        require.install(importer);
    }

    public boolean hasRequire(){
        return require != null;
    }

    public Object eval(@NotNull String source) throws ScriptException{
        return eval(importer, source, toString());
    }

    public Object eval(@NotNull Scriptable scope, @NotNull String source, @NotNull String sourceName) throws ScriptException{
        try{
            return ctx.evaluateString(scope, source, sourceName, 1, null);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Script compileScript(@NotNull String source, @NotNull String sourceName){
        return ctx.compileString(source, sourceName, 1, null);
    }

    public Script compileScript(@NotNull Reader reader, @NotNull String sourceName) throws IOException{
        return ctx.compileReader(reader, sourceName, 1, null);
    }

    public Function compileFunction(@NotNull Scriptable scope, @NotNull String source, @NotNull String sourceName){
        return ctx.compileFunction(scope, source, sourceName, 1, null);
    }

    public Object invoke(@NotNull Function function, Object... args) throws ScriptException{
        return invoke(function, importer, args);
    }

    public Object invoke(@NotNull Function function, @NotNull Scriptable scope, Object... args) throws ScriptException{
        try{
            return function.call(ctx, scope, scope, args);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(@NotNull Script script) throws ScriptException{
        return exec(script, importer);
    }

    public Object exec(@NotNull Script script, @NotNull Scriptable scope) throws ScriptException{
        try{
            return script.exec(ctx, scope);
        }catch(Exception | BlockingScriptError e){
            throw new ScriptException(e);
        }
    }

    public Object exec(@NotNull Fi file) throws IOException, ScriptException{
        return exec(file.file());
    }

    public Object exec(@NotNull File file) throws IOException, ScriptException{
        try(InputStream stream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(stream)){
            return exec(compileScript(reader, file.getName()));
        }
    }

    public @NotNull Context getContext(){
        return ctx;
    }

    public @NotNull ImporterTopLevel getImporter(){
        return importer;
    }

    @Override
    public @NotNull String toString(){
        return "engine@" + Integer.toHexString(hashCode()) + ".js";
    }

    @Override
    public void close(){
        Context.exit();
        threadInstance.remove();
    }

    @FunctionalInterface
    public interface ScriptEngineFactory{
        ScriptEngineFactory DEFAULT = () -> {
            Context context = Context.getCurrentContext();
            if(context == null) context = Context.enter();
            return new JavaScriptEngine(context);
        };

        @NotNull JavaScriptEngine makeEngine();
    }
}
