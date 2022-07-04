package fr.xpdustry.distributor.message;

import arc.audio.*;
import org.jetbrains.annotations.*;

final class EmptyMediaReceiver implements MediaReceiver {

  static final EmptyMediaReceiver INSTANCE = new EmptyMediaReceiver();

  private EmptyMediaReceiver() {
    // Il a fait prout...
  }

  @Override
  public void sendMessage(final @NotNull String content) {
  }

  @Override
  public void sendAnnouncement(final @NotNull String content) {
  }

  @Override
  public void sendNotification(final @NotNull String content, final int icon) {
  }

  @Override
  public void sendPopup(final @NotNull String content, final float duration, final int alignement, final int offsetX, final int offsetY) {
  }

  @Override
  public void sendLabel(final @NotNull String content, final float duration, final float x, final float y) {
  }

  @Override
  public void sendWarning(final @NotNull String content) {
  }

  @Override
  public void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
  }

  @Override
  public void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan) {
  }
}
