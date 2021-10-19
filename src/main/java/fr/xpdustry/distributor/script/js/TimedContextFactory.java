package fr.xpdustry.distributor.script.js;

import fr.xpdustry.distributor.exception.*;

import org.mozilla.javascript.*;


/** @see org.mozilla.javascript.ContextFactory */
public class TimedContextFactory extends ContextFactory{
    private final int maxRuntimeDuration;

    public TimedContextFactory(int maxRuntimeDuration){
        this.maxRuntimeDuration = maxRuntimeDuration;
    }

    @Override
    protected Context makeContext(){
        TimedContext cx = new TimedContext(this);
        // Make Rhino runtime to call observeInstructionCount
        // each 10000 bytecode instructions
        cx.setInstructionObserverThreshold(10000);
        return cx;
    }

    @Override
    public boolean hasFeature(Context cx, int featureIndex){
        // Turn on maximum compatibility with MSIE scripts
        switch(featureIndex){
            case Context.FEATURE_NON_ECMA_GET_YEAR:
            case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
            case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
                return true;

            case Context.FEATURE_PARENT_PROTO_PROPERTIES:
                return false;
        }

        return super.hasFeature(cx, featureIndex);
    }

    @Override
    protected void observeInstructionCount(Context cx, int instructionCount){
        TimedContext tcx = (TimedContext)cx;
        long currentTime = System.currentTimeMillis();
        if(currentTime - tcx.startTime > maxRuntimeDuration * 1000L){
            throw new BlockingScriptError(Thread.currentThread());
        }
    }

    @Override
    protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args){
        TimedContext tcx = (TimedContext)cx;
        tcx.startTime = System.currentTimeMillis();
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }

    /** Custom Context to store execution time. */
    private static class TimedContext extends Context{
        private long startTime;

        public TimedContext(ContextFactory factory){
            super(factory);
        }
    }
}
