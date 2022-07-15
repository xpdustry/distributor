package fr.xpdustry.distributor.event;

public interface EventListener<E> {

  void onEvent(final E event);
}
