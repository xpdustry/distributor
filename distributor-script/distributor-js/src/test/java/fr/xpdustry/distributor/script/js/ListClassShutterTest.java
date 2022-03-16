package fr.xpdustry.distributor.script.js;

import arc.struct.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ListClassShutterTest {

  private static final String EXAMPLE_PACKAGE = "fr.xpdustry.distributor.command";
  private static final String EXAMPLE_CLASS_1 = "fr.xpdustry.distributor.command.ArcPermission";
  private static final String EXAMPLE_CLASS_2 = "fr.xpdustry.distributor.command.ArcCommandManager";

  @Test
  void test_empty_list_class_shutter() {
    final var classShutter = new ListClassShutter(Seq.with(), Seq.with());

    assertTrue(classShutter.visibleToScripts(EXAMPLE_PACKAGE));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_1));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_2));
  }

  @Test
  void test_blacklisted_class() {
    final var classShutter = new ListClassShutter(Seq.with(EXAMPLE_CLASS_1), Seq.with());

    assertTrue(classShutter.visibleToScripts(EXAMPLE_PACKAGE));
    assertFalse(classShutter.visibleToScripts(EXAMPLE_CLASS_1));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_2));
  }

  @Test
  void test_blacklisted_package() {
    final var classShutter = new ListClassShutter(Seq.with(EXAMPLE_PACKAGE), Seq.with());

    assertFalse(classShutter.visibleToScripts(EXAMPLE_PACKAGE));
    assertFalse(classShutter.visibleToScripts(EXAMPLE_CLASS_1));
    assertFalse(classShutter.visibleToScripts(EXAMPLE_CLASS_2));
  }

  @Test
  void test_whitelisted_class() {
    final var classShutter = new ListClassShutter(Seq.with(EXAMPLE_PACKAGE), Seq.with(EXAMPLE_CLASS_1));

    assertFalse(classShutter.visibleToScripts(EXAMPLE_PACKAGE));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_1));
    assertFalse(classShutter.visibleToScripts(EXAMPLE_CLASS_2));
  }

  @Test
  void test_whitelisted_package() {
    final var classShutter = new ListClassShutter(Seq.with("fr.xpdustry.distributor"), Seq.with(EXAMPLE_PACKAGE));

    assertTrue(classShutter.visibleToScripts(EXAMPLE_PACKAGE));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_1));
    assertTrue(classShutter.visibleToScripts(EXAMPLE_CLASS_2));
    assertFalse(classShutter.visibleToScripts("fr.xpdustry.distributor.Distributor"));
  }
}
