package fr.xpdustry.distributor.legacy.util;

import mindustry.gen.*;

public class TestPlayer extends Player {

  public TestPlayer() {
  }

  @Override
  public void sendMessage(String text) {
    lastText = text;
  }
}
