package fr.xpdustry.distributor.event;


interface EventRunner<T>{
    void run(T event);
}
