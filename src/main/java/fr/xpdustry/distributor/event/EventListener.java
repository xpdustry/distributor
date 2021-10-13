package fr.xpdustry.distributor.event;

interface EventListener<T>{
    void trigger(T event);
}
