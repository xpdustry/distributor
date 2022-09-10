package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;
import fr.xpdustry.distributor.text.serializer.*;
import io.leangen.geantyref.*;
import java.util.*;
import mindustry.gen.*;

final class PlayerAudience implements Audience {

  private final Player player;
  private final MetadataContainer metas;

  PlayerAudience(final Player player) {
    this.player = player;
    this.metas = MetadataContainer.builder()
      .withConstant(StandardKeys.UUID, this.player.uuid())
      .withConstant(StandardKeys.USID, this.player.usid())
      .withConstant(StandardKeys.LOCALE, getPlayerLocale())
      .withConstant(StandardKeys.NAME, getRealPlayerName())
      .withSupplier(StandardKeys.DISPLAY_NAME, this.player::name)
      .withSupplier(StandardKeys.TEAM, this.player::team)
      .build();
  }

  @Override
  public void sendMessage(final Component component) {
    Call.sendMessage(player.con(), ComponentSerializer.client().serialize(component, this.metas), null, null);
  }

  @Override
  public void sendWarning(final Component component) {
    Call.announce(player.con(), ComponentSerializer.client().serialize(component, this.metas));
  }

  @Override
  public void sendAnnouncement(final Component component) {
    Call.infoMessage(player.con(), ComponentSerializer.client().serialize(component, this.metas));
  }

  @Override
  public void sendNotification(final Component component, final char icon) {
    Call.warningToast(player.con(), icon, ComponentSerializer.client().serialize(component, this.metas));
  }

  @Override
  public void sendNotification(final Component component) {
    Call.warningToast(player.con(), 0, ComponentSerializer.client().serialize(component, this.metas));
  }

  @Override
  public void playSound(final Sound sound, final float volume, final float pitch, final float x, final float y) {
    Call.soundAt(player.con(), sound, x, y, volume, pitch);
  }

  @Override
  public void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
    Call.sound(player.con(), sound, volume, pitch, pan);
  }

  @Override
  public void showHud(final Component component) {
    Call.setHudText(ComponentSerializer.client().serialize(component, this.metas));
  }

  @Override
  public void hideHud() {
    Call.hideHudText(player.con());
  }

  @Override
  public MetadataContainer getMetadata() {
    return metas;
  }

  private Locale getPlayerLocale() {
    final var parts = this.player.locale().split("-", 3);
    return switch (parts.length) {
      case 1 -> new Locale(parts[0]);
      case 2 -> new Locale(parts[0], parts[1]);
      case 3 -> new Locale(parts[0], parts[1], parts[2]);
      default -> Locale.getDefault();
    };
  }

  private String getRealPlayerName() {
    return this.player.getInfo().lastName;
  }
}
