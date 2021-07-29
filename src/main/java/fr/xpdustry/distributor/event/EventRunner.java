package fr.xpdustry.distributor.event;

import arc.func.*;


public interface EventRunner extends Cons<Runnable>{
    EventRunner defaultRunner = Runnable::run;

    @Override
    void get(Runnable runnable);
}