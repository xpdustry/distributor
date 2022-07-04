package fr.xpdustry.distributor.message;

import arc.audio.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public final class V7PlayerMediaReceiver extends PlayerMediaReceiver {

  public V7PlayerMediaReceiver(final @NotNull Player player) {
    super(player);
  }

  @Override
  public void playSound(final @NotNull Sound sound, final float volume, final float pitch, final float pan, final float x, final float y) {
    Call.soundAt(sound, x, y, volume, pitch);
  }

  @Override
  public void playSound(@NotNull Sound sound, float volume, float pitch, float pan) {
    Call.sound(sound, volume, pitch, pan);
  }
}
