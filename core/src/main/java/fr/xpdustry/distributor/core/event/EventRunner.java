package fr.xpdustry.distributor.core.event;

import arc.func.*;

public interface EventRunner extends Cons<Runnable>{
    EventRunner defaultRunner = Runnable::run;

    @Override
    void get(Runnable runnable);
}