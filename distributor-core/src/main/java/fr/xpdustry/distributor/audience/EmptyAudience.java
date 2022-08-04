package fr.xpdustry.distributor.audience;

import java.util.function.*;

final class EmptyAudience implements Audience {

  static final EmptyAudience INSTANCE = new EmptyAudience();

  private EmptyAudience() {
  }

  @Override
  public void forEachAudience(Consumer<? super Audience> action) {
  }

  @Override
  public Audience filterAudience(Predicate<Audience> predicate) {
    return this;
  }
}
