package fr.xpdustry.distributor.event;

public interface EventRunner{
    void run(Runnable runnable);

    EventRunner DEFAULT = Runnable::run;
}
