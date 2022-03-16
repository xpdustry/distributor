package fr.xpdustry.distributor.localization;

import java.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceBundleTranslatorTest {

  private static final ResourceBundleTranslator translator =
    new ResourceBundleTranslator("bundles/test", ResourceBundleTranslatorTest.class.getClassLoader());

  @BeforeAll
  public static void setup() {
    Locale.setDefault(Locale.ROOT);
  }

  @Test
  public void test_translator_strings() {
    assertEquals("Bonjour", translator.translate("arrival", Locale.FRENCH));
    assertEquals("Goodbye", translator.translate("departure", Locale.FRENCH));

    assertEquals("Hello", translator.translate("arrival", Locale.ROOT));
    assertEquals("Goodbye", translator.translate("departure", Locale.ROOT));

    assertNull(translator.translate("comeback", Locale.ROOT));
  }
}
