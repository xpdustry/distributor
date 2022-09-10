package fr.xpdustry.distributor.event;

import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractEventBus implements EventBus {

  private static final Comparator<EventSubscriber<?>> COMPARATOR = Comparator.comparing(EventSubscriber::getPriority);

  private final Set<Object> subscribed = new HashSet<>();

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public EventPostResult post(Object event) {
    Map<EventSubscriber<?>, Throwable> exceptions = null;
    final var subscribers = getEventSubscribers(event.getClass());
    subscribers.sort(COMPARATOR);
    for (final EventSubscriber subscriber : subscribers) {
      try {
        subscriber.onEvent(event);
      } catch (final Throwable throwable) {
        if (exceptions == null) {
          exceptions = new HashMap<>();
        }
        exceptions.put(subscriber, throwable);
      }
    }
    return exceptions == null ? EventPostResult.success() : EventPostResult.failure(exceptions);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public EventSubscription subscribe(final Object object) {
    if (!subscribed.add(object)) {
      return () -> {};
    }

    final var subscriptions = new ArrayList<EventSubscription>();
    for (final var method : object.getClass().getDeclaredMethods()) {
      final var annotation = method.getAnnotation(EventHandler.class);
      if (annotation == null) {
        continue;
      } else if (method.getParameterCount() != 1) {
        throw new IllegalArgumentException("The event handler on " + method + " hasn't the right parameter count.");
      } else if (!method.canAccess(object) || !method.trySetAccessible()) {
        throw new RuntimeException("Unable to make " + method + " accessible.");
      }

      subscriptions.add(
        subscribe(method.getParameterTypes()[0], new MethodEventSubscriber(object, method, annotation.priority()))
      );
    }

    return () -> subscriptions.forEach(EventSubscription::unsubscribe);
  }

  protected abstract List<EventSubscriber<?>> getEventSubscribers(final Class<?> clazz);

  private static final class MethodEventSubscriber<E> implements EventSubscriber<E> {

    private final Object target;
    private final Method method;
    private final EventPriority priority;

    private MethodEventSubscriber(final Object target, final Method method, final EventPriority priority) {
      this.target = target;
      this.method = method;
      this.priority = priority;
    }

    @Override
    public void onEvent(final E event) {
      try {
        this.method.invoke(event);
      } catch (final ReflectiveOperationException e) {
        throw new RuntimeException("Failed to call " + this.method + " on " + this.target, e);
      }
    }

    @Override
    public EventPriority getPriority() {
      return priority;
    }
  }
}
