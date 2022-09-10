package fr.xpdustry.distributor;

import arc.util.*;

public class Main {

  public static void main(String[] args) {
    Timer.instance();
    Timer.schedule(() -> {}, 0);

    Timer.instance().start();
  }
}
