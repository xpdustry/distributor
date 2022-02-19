package fr.xpdustry.distributor.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import fr.xpdustry.distributor.localization.ResourceBundleTranslator;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


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
