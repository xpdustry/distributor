package fr.xpdustry.distributor.exception;


public class BlockingScriptError extends Error{
    private final Thread blockedThread;

    public BlockingScriptError(Thread blockedThread){
        super();
        this.blockedThread = blockedThread;
    }

    public Thread getBlockedThread(){
        return blockedThread;
    }
}
