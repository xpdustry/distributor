package fr.xpdustry.distributor.message;

import arc.audio.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ForwardingMediaReceiver extends MediaReceiver {

  @NotNull Iterable<MediaReceiver> receivers();

  @Override
  default void sendMessage(final @NotNull String content) {
    for (final var receiver : receivers()) receiver.sendMessage(content);
  }

  default void sendAnnouncement(final @NotNull String content) {
    for (final var receiver : receivers()) receiver.sendAnnouncement(content);
  }

  default void sendNotification(final @NotNull String content, final int icon) {
    for (final var receiver : receivers()) receiver.sendNotification(content, icon);
  }

  default void sendPopup(final @NotNull String content, final float duration, final int alignement, final int offsetX, final int offsetY) {
    for (final var receiver : receivers()) receiver.sendPopup(content, duration, alignement, offsetX, offsetY);
  }

  default void sendLabel(final @NotNull String content, final float duration, final float x, final float y) {
    for (final var receiver : receivers()) receiver.sendLabel(content, duration, x, y);
  }

  default void sendWarning(final @NotNull String content) {
    for (final var receiver : receivers()) receiver.sendWarning(content);
  }

  default void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
    for (final var receiver : receivers()) receiver.playSound(sound, volume, pitch, pan, x, y);
  }

  default void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan) {
    for (final var receiver : receivers()) receiver.playSound(sound, volume, pitch, pan);
  }
}
