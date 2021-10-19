package fr.xpdustry.distributor.event;


public class EventListener<T>{
    private final Object event;
    private final EventRunner<T> runner;

    public EventListener(Class<T> event, EventRunner<T> runner){
        this.event = event;
        this.runner = runner;
    }

    public EventListener(T event, Runnable runner){
        this.event = event;
        this.runner = e -> runner.run();
    }

    public void trigger(T type){
        runner.run(type);
    }

    public Object getEvent(){
        return event;
    }
}
