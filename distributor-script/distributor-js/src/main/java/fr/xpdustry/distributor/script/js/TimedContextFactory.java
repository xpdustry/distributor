package fr.xpdustry.distributor.script.js;

import fr.xpdustry.distributor.exception.*;

import org.jetbrains.annotations.*;
import rhino.*;


/** @see rhino.ContextFactory */
public class TimedContextFactory extends ContextFactory{
    private final int maxRuntime;

    public TimedContextFactory(int maxRuntime){
        this.maxRuntime = maxRuntime;
    }

    @Override protected Context makeContext(){
        final var ctx = new TimedContext(this);
        ctx.setInstructionObserverThreshold(10000);
        return ctx;
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    @Override public boolean hasFeature(Context cx, int featureIndex){
        switch(featureIndex){
            case Context.FEATURE_DYNAMIC_SCOPE:
            case Context.FEATURE_NON_ECMA_GET_YEAR:
            case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
            case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
                return true;
            case Context.FEATURE_PARENT_PROTO_PROPERTIES:
                return false;
            default:
                return super.hasFeature(cx, featureIndex);
        }
    }

    @Override protected Object doTopCall(Callable callable, Context ctx, Scriptable scope, Scriptable thisObj, Object[] args){
        final var tcx = (TimedContext)ctx;
        tcx.startTime = System.currentTimeMillis();
        return super.doTopCall(callable, tcx, scope, thisObj, args);
    }

    @Override protected void observeInstructionCount(Context ctx, int instructionCount){
        final var tcx = (TimedContext)ctx;
        final var currentTime = System.currentTimeMillis();
        if(currentTime - tcx.startTime > maxRuntime * 1000L){
            throw new BlockingScriptError();
        }
    }

    /** Custom {@link Context} to store execution time. */
    public static class TimedContext extends Context{
        private long startTime;

        public TimedContext(final @NotNull ContextFactory factory){
            super(factory);
        }

        public long getStartTime(){
            return startTime;
        }
    }
}
