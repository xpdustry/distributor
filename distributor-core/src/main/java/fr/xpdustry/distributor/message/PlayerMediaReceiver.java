package fr.xpdustry.distributor.message;

import arc.audio.*;
import mindustry.gen.*;
import mindustry.net.*;
import org.jetbrains.annotations.*;

public class PlayerMediaReceiver implements MediaReceiver {

  protected final NetConnection connection;

  public PlayerMediaReceiver(final @NotNull Player player) {
    this.connection = player.con();
  }

  @Override
  public void sendMessage(final @NotNull String content) {
    Call.sendMessage(connection, content, null, null);
  }

  @Override
  public void sendAnnouncement(final @NotNull String content) {
    Call.infoMessage(connection, content);
  }

  @Override
  public void sendNotification(final @NotNull String content, int icon) {
    Call.warningToast(connection, icon, content);
  }

  @Override
  public void sendPopup(final @NotNull String content, final float duration, final int alignement, final int offsetX, final int offsetY) {
    final int t = Math.max(offsetY, 0);
    final int l = -Math.min(offsetX, 0);
    final int b = -Math.min(offsetY, 0);
    final int r = Math.max(offsetX, 0);
    Call.infoPopup(connection, content, duration, alignement, t, l, b, r);
  }

  @Override
  public void sendLabel(final @NotNull String content, final float duration, final float x, final float y) {
    Call.label(connection, content, duration, x, y);
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    Call.announce(connection, content);
  }

  @Override
  public void playSound(@NotNull Sound sound, float volume, float pitch, float pan, float x, float y) {
  }

  @Override
  public void playSound(@NotNull Sound sound, float volume, float pitch, float pan) {
  }
}
