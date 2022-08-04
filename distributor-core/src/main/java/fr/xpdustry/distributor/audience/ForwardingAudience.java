package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.text.*;
import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface ForwardingAudience extends Audience {

  Iterable<Audience> getAudiences();

  @Override
  default void sendMessage(final Component component) {
    for (final var audience : getAudiences()) {
      audience.sendMessage(component);
    }
  }

  @Override
  default void sendWarning(final Component component) {
    for (final var audience : getAudiences()) {
      audience.sendWarning(component);
    }
  }

  @Override
  default void sendAnnouncement(final Component component) {
    for (final var audience : getAudiences()) {
      audience.sendAnnouncement(component);
    }
  }

  @Override
  default void sendNotification(final Component component, final char icon) {
    for (final var audience : getAudiences()) {
      audience.sendNotification(component, icon);
    }
  }

  @Override
  default void sendNotification(final Component component) {
    for (final var audience : getAudiences()) {
      audience.sendNotification(component);
    }
  }

  @Override
  default void playSound(final Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
    for (final var audience : getAudiences()) {
      audience.playSound(sound, volume, pitch, pan, x, y);
    }
  }

  @Override
  default void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
    for (final var audience : getAudiences()) {
      audience.playSound(sound, volume, pitch, pan);
    }
  }

  @Override
  default void setHud(final Component component) {
    for (final var audience : getAudiences()) {
      audience.setHud(component);
    }
  }

  @Override
  default void clearHud() {
    for (final var audience : getAudiences()) {
      audience.clearHud();
    }
  }

  @Override
  default Audience filterAudience(final Predicate<Audience> predicate) {
    final var audiences = new ArrayList<Audience>();
    for (final var audience : getAudiences()) {
      final var filtered = audience.filterAudience(predicate);
      if (filtered == Audience.empty()) {
        continue;
      }
      audiences.add(audience);
    }
    return audiences.isEmpty()
      ? Audience.empty()
      : (ForwardingAudience) () -> audiences;
  }

  @Override
  default void forEachAudience(final Consumer<? super Audience> action) {
    for (final var audience : getAudiences()) {
      audience.forEachAudience(action);
    }
  }
}
