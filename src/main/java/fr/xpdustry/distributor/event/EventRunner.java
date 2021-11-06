package fr.xpdustry.distributor.event;


public interface EventRunner<T>{
    void run(T event);
}
