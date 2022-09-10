package fr.xpdustry.distributor.audience;

import java.util.stream.*;

final class EmptyAudience implements Audience {

  static final EmptyAudience INSTANCE = new EmptyAudience();

  private EmptyAudience() {
  }

  @Override
  public Stream<Audience> toStream() {
    return Stream.empty();
  }
}
