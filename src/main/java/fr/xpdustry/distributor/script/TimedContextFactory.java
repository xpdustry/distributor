package fr.xpdustry.distributor.script;

import fr.xpdustry.distributor.exception.*;

import org.mozilla.javascript.*;

import static org.mozilla.javascript.Context.*;


/** @see org.mozilla.javascript.ContextFactory */
public class TimedContextFactory extends ContextFactory{
    private final int maxRuntime;

    public TimedContextFactory(int maxRuntime){
        this.maxRuntime = maxRuntime;
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
        if(currentTime - tcx.startTime > maxRuntime * 1000L){
            throw new BlockingScriptError();
        }
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
}
