package fr.xpdustry.distributor.script;

import arc.*;

import fr.xpdustry.distributor.exception.*;

import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;

import static java.util.Objects.requireNonNull;
import static org.mozilla.javascript.Context.*;


/** @see org.mozilla.javascript.ContextFactory */
public class TimedContextFactory extends ContextFactory{
    private final int maxRuntimeDuration;

    public TimedContextFactory(int maxRuntimeDuration){
        this.maxRuntimeDuration = maxRuntimeDuration;
    }

    @Override
    protected Context makeContext(){
        TimedContext ctx = new TimedContext(this);
        ctx.setInstructionObserverThreshold(10000);
        return ctx;
    }

    @Override
    public boolean hasFeature(Context cx, int featureIndex){
        switch(featureIndex){
            case FEATURE_DYNAMIC_SCOPE:
            case FEATURE_NON_ECMA_GET_YEAR:
            case FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
            case FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
                return true;
            case FEATURE_PARENT_PROTO_PROPERTIES:
                return false;
            default:
                return super.hasFeature(cx, featureIndex);
        }
    }

    @Override
    protected Object doTopCall(Callable callable, Context ctx, Scriptable scope, Scriptable thisObj, Object[] args){
        TimedContext tcx = (TimedContext)ctx;
        tcx.startTime = System.currentTimeMillis();
        return super.doTopCall(callable, tcx, scope, thisObj, args);
    }

    @Override
    protected void observeInstructionCount(Context ctx, int instructionCount){
        TimedContext tcx = (TimedContext)ctx;
        long currentTime = System.currentTimeMillis();
        if(currentTime - tcx.startTime > maxRuntimeDuration * 1000L){
            throw new BlockingScriptError();
        }
    }

    @Override
    protected void onContextCreated(Context cx){
        Events.fire(new ContextCreateEvent(cx));
    }

    @Override
    protected void onContextReleased(Context cx){
        Events.fire(new ContextReleaseEvent(cx));
    }

    /** Custom Context to store execution time. */
    public static class TimedContext extends Context{
        private long startTime;

        public TimedContext(ContextFactory factory){
            super(factory);
        }

        public long getStartTime(){
            return startTime;
        }
    }

    public record ContextCreateEvent(@NotNull Context context){
        public ContextCreateEvent{
            requireNonNull(context, "context can't be null.");
        }
    }

    public record ContextReleaseEvent(@NotNull Context context){
        public ContextReleaseEvent{
            requireNonNull(context, "context can't be null.");
        }
    }
}
