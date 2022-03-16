package fr.xpdustry.distributor.script.js;

import fr.xpdustry.distributor.exception.*;
import java.io.*;
import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;

public final class RhinoJavaScriptEngine {

  private static final String EVAL_FILENAME = "eval";
  private @Nullable Scriptable globalScope;

  public @NotNull Object eval(final @NotNull String eval, final @Nullable Scriptable scope) throws ScriptException {
    try (final Context cx = createContext()) {
      final var localScope = initScope(cx, scope);
      final var result = cx.evaluateString(localScope, eval, EVAL_FILENAME, 0, null);
      return Context.jsToJava(result, Object.class);
    } catch (final RhinoException | BlockingScriptError e) {
      throw new ScriptException(e);
    }
  }

  public @UnknownNullability Object eval(final @NotNull String eval) throws ScriptException {
    return eval(eval, null);
  }

  public @UnknownNullability Object eval(final @NotNull Reader reader, final @Nullable Scriptable scope) throws ScriptException {
    try (final Context cx = createContext()) {
      final var localScope = initScope(cx, scope);
      final var result = cx.evaluateReader(localScope, reader, EVAL_FILENAME, 0, null);
      return Context.jsToJava(result, Object.class);
    } catch (final RhinoException | BlockingScriptError | IOException e) {
      throw new ScriptException(e);
    }
  }

  public @UnknownNullability Object eval(final @NotNull Reader reader) throws ScriptException {
    return eval(reader, null);
  }

  public @UnknownNullability Object eval(final @NotNull Script script, final @Nullable Scriptable scope) throws ScriptException {
    try (final Context cx = createContext()) {
      final var localScope = initScope(cx, scope);
      final var result = script.exec(cx, localScope);
      return Context.jsToJava(result, Object.class);
    } catch (final RhinoException | BlockingScriptError e) {
      throw new ScriptException(e);
    }
  }

  public @UnknownNullability Object eval(final @NotNull Script script) throws ScriptException {
    return eval(script, null);
  }

  public @NotNull Script compile(final @NotNull String script) throws ScriptException {
    try (final Context cx = createContext()) {
      return cx.compileString(script, EVAL_FILENAME, 1, null);
    } catch (final RhinoException e) {
      throw new ScriptException(e);
    }
  }

  public @NotNull Script compile(final @NotNull Reader reader) throws ScriptException {
    try (final Context cx = createContext()) {
      return cx.compileReader(reader, EVAL_FILENAME, 1, null);
    } catch (final RhinoException | IOException e) {
      throw new ScriptException(e);
    }
  }

  public void installRequire(final @NotNull ModuleScriptProvider provider) {
    try (final Context cx = createContext()) {
      new RequireBuilder()
        .setSandboxed(false)
        .setModuleScriptProvider(provider)
        .createRequire(cx, getGlobalScope(cx))
        .install(getGlobalScope(cx));
    }
  }

  public @NotNull Scriptable newScope() {
    try (final Context cx = createContext()) {
      return newScope(cx);
    }
  }

  public @NotNull Scriptable newScope(final @Nullable Scriptable parent) {
    try (final Context cx = createContext()) {
      return newScope(cx, parent);
    }
  }

  private @NotNull Scriptable newScope(final @NotNull Context cx) {
    return newScope(cx, getGlobalScope(cx));
  }

  private @NotNull Scriptable newScope(final @NotNull Context cx, final @Nullable Scriptable parent) {
    final var scope = cx.newObject(parent);
    scope.setPrototype(parent);
    scope.setParentScope(null);
    return scope;
  }

  public @NotNull Scriptable getGlobalScope() {
    try (final Context cx = createContext()) {
      return getGlobalScope(cx);
    }
  }

  private @NotNull Scriptable getGlobalScope(final @NotNull Context cx) {
    if (globalScope == null) globalScope = new ImporterTopLevel(cx);
    return globalScope;
  }

  private @NotNull Scriptable initScope(final @NotNull Context cx, final @Nullable Scriptable scope) {
    return scope == null ? newScope(cx, getGlobalScope(cx)) : scope;
  }

  private @NotNull Context createContext() {
    final var cx = ContextFactory.getGlobal().enterContext();
    cx.setLanguageVersion(Context.VERSION_ES6);
    cx.setOptimizationLevel(9);
    cx.setGeneratingDebug(true);
    return cx;
  }
}
