package fr.xpdustry.distributor.message;

import arc.audio.*;
import arc.util.*;
import org.jetbrains.annotations.*;

final class ConsoleMediaReceiver implements MediaReceiver {

  static final ConsoleMediaReceiver INSTANCE = new ConsoleMediaReceiver();

  private ConsoleMediaReceiver() {
  }

  @Override
  public void sendMessage(final @NotNull String content) {
    Log.info(content);
  }

  @Override
  public void sendAnnouncement(final @NotNull String content) {
  }

  @Override
  public void sendNotification(final @NotNull String content, int icon) {
  }

  @Override
  public void sendPopup(final @NotNull String content, final float duration, final int alignement, final int offsetX, final int offsetY) {
  }

  @Override
  public void sendLabel(final @NotNull String content, final float duration, final float x, final float y) {
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    Log.warn(content);
  }

  @Override
  public void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
  }

  @Override
  public void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan) {
  }
}
