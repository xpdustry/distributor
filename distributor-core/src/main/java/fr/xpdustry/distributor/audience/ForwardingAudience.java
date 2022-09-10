package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.text.*;
import java.util.stream.*;

@FunctionalInterface
public interface ForwardingAudience extends Audience {

  Iterable<Audience> getAudiences();

  @Override
  default void sendMessage(final Component component) {
    for (final var audience : this.getAudiences()) {
      audience.sendMessage(component);
    }
  }

  @Override
  default void sendWarning(final Component component) {
    for (final var audience : this.getAudiences()) {
      audience.sendWarning(component);
    }
  }

  @Override
  default void sendAnnouncement(final Component component) {
    for (final var audience : this.getAudiences()) {
      audience.sendAnnouncement(component);
    }
  }

  @Override
  default void sendNotification(final Component component, final char icon) {
    for (final var audience : this.getAudiences()) {
      audience.sendNotification(component, icon);
    }
  }

  @Override
  default void sendNotification(final Component component) {
    for (final var audience : this.getAudiences()) {
      audience.sendNotification(component);
    }
  }

  @Override
  default void playSound(final Sound sound, final float volume, final float pitch, final float x, final float y) {
    for (final var audience : this.getAudiences()) {
      audience.playSound(sound, volume, pitch, x, y);
    }
  }

  @Override
  default void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
    for (final var audience : this.getAudiences()) {
      audience.playSound(sound, volume, pitch, pan);
    }
  }

  @Override
  default void showHud(final Component component) {
    for (final var audience : this.getAudiences()) {
      audience.showHud(component);
    }
  }

  @Override
  default void hideHud() {
    for (final var audience : this.getAudiences()) {
      audience.hideHud();
    }
  }
}
