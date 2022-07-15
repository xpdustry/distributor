package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.meta.MetaKey;
import fr.xpdustry.distributor.meta.MetaProvider;
import fr.xpdustry.distributor.text.Component;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Audience extends MetaProvider {

  static Audience with(final Audience... receivers) {
    return switch (receivers.length) {
      case 0 -> empty();
      case 1 -> receivers[0];
      default -> with(List.of(receivers));
    };
  }

  static ForwardingAudience with(final Iterable<Audience> receivers) {
    return () -> receivers;
  }

  static Audience empty() {
    return EmptyAudience.INSTANCE;
  }

  default void sendMessage(final Component component) {
  }

  default void sendWarning(final Component component) {
  }

  default void sendAnnouncement(final Component component) {
  }

  default void sendNotification(final Component component, final char icon) {
  }

  default void sendNotification(final Component component) {
  }

  default void playSound(final Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
  }

  default void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
  }

  default void setHud(final Component component) {
  }

  default void clearHud() {
  }

  default Audience filterAudience(final Predicate<Audience> predicate) {
    return predicate.test(this) ? this : empty();
  }

  default void forEachAudience(final Consumer<? super Audience> action) {
    action.accept(this);
  }

  @Override
  default <T> Optional<T> getMeta(final MetaKey<T> key) {
    return Optional.empty();
  }
}
