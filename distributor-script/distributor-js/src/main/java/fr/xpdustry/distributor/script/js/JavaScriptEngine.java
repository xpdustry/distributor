package fr.xpdustry.distributor.script.js;

import fr.xpdustry.distributor.exception.BlockingScriptError;
import fr.xpdustry.distributor.exception.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

public class JavaScriptEngine implements AutoCloseable {

  private static Supplier<JavaScriptEngine> factory = () -> {
    var ctx = Context.getCurrentContext();
    if (ctx == null) ctx = Context.enter();
    return new JavaScriptEngine(ctx);
  };

  private static final ThreadLocal<JavaScriptEngine> INSTANCE = ThreadLocal.withInitial(() -> JavaScriptEngine.factory.get());

  private final Context context;
  private final Scriptable globalScope;
  private @Nullable Require require = null;

  public JavaScriptEngine(final @NotNull Context context, final @NotNull Scriptable globalScope) {
    this.context = context;
    this.globalScope = globalScope;
  }

  public JavaScriptEngine(final @NotNull Context context) {
    this(context, new ImporterTopLevel(context));
  }

  public static Supplier<JavaScriptEngine> getGlobalFactory() {
    return factory;
  }

  public static void setGlobalFactory(final @NotNull Supplier<@NotNull JavaScriptEngine> factory) {
    JavaScriptEngine.factory = factory;
  }

  /**
   * @return the current or a new instance of {@code ScriptEngine}
   */
  public static JavaScriptEngine getInstance() {
    return INSTANCE.get();
  }

  /**
   * Extract the toString value of a javaScript object.
   *
   * @param obj the output object of a javascript function/script/evaluation.
   * @return a string representation of the object
   */
  public static String toString(@Nullable Object obj) {
    if (obj instanceof NativeJavaObject n) {
      obj = n.unwrap();
    }
    if (obj instanceof Undefined) {
      obj = "undefined";
    }
    return String.valueOf(obj);
  }

  public @NotNull Scriptable newScope() {
    return newScope(globalScope);
  }

  public @NotNull Scriptable newScope(final @NotNull Scriptable parent) {
    final var scope = context.newObject(parent);
    // Ensures that definitions in the root scope are found.
    scope.setPrototype(parent);
    // Ensures that new global variables are created in this scope (don't use var for them!)
    scope.setParentScope(null);
    return scope;
  }

  public void setupRequire(final @NotNull ModuleScriptProvider provider) {
    require = new RequireBuilder()
      .setSandboxed(false)
      .setModuleScriptProvider(provider)
      .createRequire(context, globalScope);
    require.install(globalScope);
  }

  public final boolean hasRequire() {
    return require != null;
  }

  public @Nullable Require getRequire() {
    return require;
  }

  public @Nullable Object eval(
    final @NotNull Scriptable scope,
    final @NotNull String source,
    final @NotNull String sourceName
  ) throws ScriptException {
    try {
      return context.evaluateString(scope, source, sourceName, 1, null);
    } catch (Exception | BlockingScriptError e) {
      throw new ScriptException(e);
    }
  }

  public @Nullable Object eval(final @NotNull String source) throws ScriptException {
    return eval(globalScope, source, toString());
  }

  public @NotNull Script compileScript(final @NotNull String source, final @NotNull String sourceName) {
    return context.compileString(source, sourceName, 1, null);
  }

  public @NotNull Script compileScript(final @NotNull Reader reader, final @NotNull String sourceName)
    throws IOException {
    return context.compileReader(reader, sourceName, 1, null);
  }

  public @NotNull Function compileFunction(
    final @NotNull Scriptable scope,
    final @NotNull String source,
    final @NotNull String sourceName
  ) {
    return context.compileFunction(scope, source, sourceName, 1, null);
  }

  public @Nullable Object invoke(final @NotNull Function function, final @Nullable Object... args)
    throws ScriptException {
    return invoke(function, globalScope, args);
  }

  public @Nullable Object invoke(
    final @NotNull Function function,
    final @NotNull Scriptable scope,
    final @Nullable Object... args
  ) throws ScriptException {
    try {
      return function.call(context, scope, scope, args);
    } catch (Exception | BlockingScriptError e) {
      throw new ScriptException(e);
    }
  }

  public @Nullable Object exec(final @NotNull Script script, final @NotNull Scriptable scope) throws ScriptException {
    try {
      return script.exec(context, scope);
    } catch (Exception | BlockingScriptError e) {
      throw new ScriptException(e);
    }
  }

  public @Nullable Object exec(final @NotNull Script script) throws ScriptException {
    return exec(script, globalScope);
  }

  public @Nullable Object exec(final @NotNull File file, final @NotNull Scriptable scope)
    throws IOException, ScriptException {
    try (final var reader = new FileReader(file, StandardCharsets.UTF_8)) {
      return exec(compileScript(reader, file.getName()), scope);
    }
  }

  public @Nullable Object exec(final @NotNull File file) throws IOException, ScriptException {
    return exec(file, globalScope);
  }

  public @NotNull Context getContext() {
    return context;
  }

  public @NotNull Scriptable getGlobalScope() {
    return globalScope;
  }

  @Override
  public @NotNull String toString() {
    return "engine@" + Integer.toHexString(hashCode()) + ".js";
  }

  @Override
  public void close() {
    Context.exit();
    INSTANCE.remove();
  }
}
