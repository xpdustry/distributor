package fr.xpdustry.distributor.audience;

import arc.audio.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.text.*;
import io.leangen.geantyref.*;
import java.util.*;
import java.util.function.*;

public interface Audience extends MetadataProvider {

  static Audience with(final Audience... receivers) {
    return switch (receivers.length) {
      case 0 -> empty();
      case 1 -> receivers[0];
      default -> with(List.of(receivers));
    };
  }

  static ForwardingAudience with(final Iterable<Audience> receivers) {
    return () -> receivers;
  }

  static Audience empty() {
    return EmptyAudience.INSTANCE;
  }

  default void sendMessage(final Component component) {
  }

  default void sendWarning(final Component component) {
  }

  default void sendAnnouncement(final Component component) {
  }

  default void sendNotification(final Component component, final char icon) {
  }

  default void sendNotification(final Component component) {
  }

  default void playSound(final Sound sound, final float volume, final float pitch, final float x, final float y) {
  }

  default void playSound(final Sound sound, final float volume, final float pitch, final float pan) {
  }

  default void setHud(final Component component) {
  }

  default void clearHud() {
  }

  default Audience filterAudience(final Predicate<Audience> predicate) {
    return predicate.test(this) ? this : empty();
  }

  default void forEachAudience(final Consumer<? super Audience> action) {
    action.accept(this);
  }

  @Override
  default <T> Optional<T> getMetadata(final String key, final TypeToken<T> type) {
    return Optional.empty();
  }
}
