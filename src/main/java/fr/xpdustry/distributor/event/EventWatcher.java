package fr.xpdustry.distributor.event;


import java.util.*;


public class EventWatcher<T> implements EventListener{
    private final Object event;
    private final EventRunner<T> runner;

    public EventWatcher(Class<T> event, EventRunner<T> runner){
        this.event = event;
        this.runner = runner;
    }

    public EventWatcher(T event, Runnable runner){
        this.event = event;
        this.runner = e -> runner.run();
    }

    protected void trigger(T type){
        runner.run(type);
    }

    public Object getEvent(){
        return event;
    }
}
