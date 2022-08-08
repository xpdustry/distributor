package fr.xpdustry.distributor.event;

import java.util.*;

@FunctionalInterface
public interface MonoEventListener<E> extends EventListener {

  void onEvent(final E event);
}
