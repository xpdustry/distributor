package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.struct.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.renderer.*;
import java.lang.reflect.*;
import java.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.net.*;
import org.checkerframework.checker.nullness.qual.*;

@SuppressWarnings("JavaReflectionMemberAccess")
final class PlayerAudience implements Audience {

  private static final @Nullable Method CALL_PLAY_SOUND;
  private static final @Nullable Method CALL_PLAY_SOUND_AT;

  static {
    if (Version.isAtLeast("135")) {
      try {
        CALL_PLAY_SOUND = Call.class.getMethod(
          "sound",
          NetConnection.class, Sound.class, float.class, float.class, float.class
        );
        CALL_PLAY_SOUND_AT = Call.class.getMethod(
          "soundAt",
          NetConnection.class, Sound.class, float.class, float.class, float.class, float.class, float.class
        );
      } catch (final NoSuchMethodException e) {
        throw new RuntimeException("Mindustry v135+ should support play sounds.", e);
      }
    } else {
      CALL_PLAY_SOUND = null;
      CALL_PLAY_SOUND_AT = null;
    }
  }

  private final Player player;
  private final MetadataContainer metas;

  PlayerAudience(final Player player) {
    this.player = player;
    this.metas = MetadataContainer.builder()
      .withConstant(StandardMetaKeys.MUUID, MUUID.of(this.player))
      .withConstant(StandardMetaKeys.LOCALE, getPlayerLocale(this.player))
      .withSupplier(StandardMetaKeys.NAME, this.player::name)
      .withSupplier(StandardMetaKeys.TEAM, this.player::team)
      .build();
  }

  @Override
  public void sendMessage(final Component component) {
    Call.sendMessage(player.con(), ComponentRenderer.client().render(component), null, null);
  }

  @Override
  public void sendWarning(final Component component) {
    Call.announce(player.con(), ComponentRenderer.client().render(component));
  }

  @Override
  public void sendAnnouncement(final Component component) {
    Call.infoMessage(player.con(), ComponentRenderer.client().render(component));
  }

  @Override
  public void sendNotification(final Component component, final char icon) {
    Call.warningToast(player.con(), icon, ComponentRenderer.client().render(component));
  }

  @Override
  public void sendNotification(final Component component) {
    Call.warningToast(player.con(), 0, ComponentRenderer.client().render(component));
  }

  @Override
  public void playSound(final Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
    if (CALL_PLAY_SOUND_AT != null) {
      try {
        CALL_PLAY_SOUND_AT.invoke(null, player.con(), sound, x, y, volume, pitch, pan);
      } catch (final ReflectiveOperationException e) {
        throw new IllegalStateException("Failed to play sound.", e);
      }
    }
  }

  @Override
  public void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
    if (CALL_PLAY_SOUND != null) {
      try {
        CALL_PLAY_SOUND.invoke(null, player.con(), sound, volume, pitch, pan);
      } catch (final ReflectiveOperationException e) {
        throw new IllegalStateException("Failed to play sound.", e);
      }
    }
  }

  @Override
  public void setHud(final Component component) {
    Call.setHudText(ComponentRenderer.client().render(component));
  }

  @Override
  public void clearHud() {
    Call.hideHudText(player.con());
  }

  @Override
  public <T> Optional<T> getMetadata(final Key<T> key) {
    return metas.getMetadata(key);
  }

  private Locale getPlayerLocale(final Player player) {
    final var parts = player.locale().split("-", 3);
    return switch (parts.length) {
      case 1 -> new Locale(parts[0]);
      case 2 -> new Locale(parts[0], parts[1]);
      case 3 -> new Locale(parts[0], parts[1], parts[2]);
      default -> Locale.getDefault();
    };
  }
}
