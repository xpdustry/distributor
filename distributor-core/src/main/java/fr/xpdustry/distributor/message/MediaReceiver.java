package fr.xpdustry.distributor.message;

import arc.audio.*;
import arc.struct.*;
import fr.xpdustry.distributor.*;
import java.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public interface MediaReceiver extends MessageReceiver {

  static @NotNull MediaReceiver of(final @NotNull MediaReceiver... receivers) {
    return switch (receivers.length) {
      case 0 -> empty();
      case 1 -> receivers[0];
      default -> of(List.of(receivers));
    };
  }

  static @NotNull MediaReceiver of(final @NotNull Iterable<MediaReceiver> receivers) {
    return (ForwardingMediaReceiver) () -> receivers;
  }

  static @NotNull MediaReceiver empty() {
    return EmptyMediaReceiver.INSTANCE;
  }

  static @NotNull MediaReceiver all() {
    return (ForwardingMediaReceiver) () -> List.of(console(), players());
  }

  static @NotNull MediaReceiver console() {
    return ConsoleMediaReceiver.INSTANCE;
  }

  static @NotNull MediaReceiver players() {
    return (ForwardingMediaReceiver) () -> Seq.with(Groups.player).map(MediaReceiver::player);
  }

  static @NotNull MediaReceiver player(final @NotNull Player player) {
    return DistributorPlugin.getRuntime().createPlayerMediaReceiverDelegate(player);
  }

  static @NotNull MediaReceiver team(final @NotNull Team team) {
    return (ForwardingMediaReceiver) () -> Seq.with(Groups.player).filter(p -> p.team().equals(team)).map(MediaReceiver::player);
  }

  void sendAnnouncement(final @NotNull String content);

  void sendNotification(final @NotNull String content, final int icon);

  default void sendNotification(final @NotNull String content) {
    sendNotification(content, 0);
  }

  void sendPopup(final @NotNull String content, final float duration, final int alignement, final int offsetX, final int offsetY);

  default void sendPopup(final @NotNull String content, final float duration, final int alignement) {
    sendPopup(content, duration, alignement, 0, 0);
  }

  void sendLabel(final @NotNull String content, final float duration, final float x, final float y);

  void sendWarning(final @NotNull String content);

  void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan, final float x, final float y);

  void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan);
}
