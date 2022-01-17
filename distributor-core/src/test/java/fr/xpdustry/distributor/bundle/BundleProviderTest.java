package fr.xpdustry.distributor.bundle;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class BundleProviderTest{
    private static final BundleProvider provider =
        l -> WrappedBundle.of("bundles/test", l, BundleProvider.class.getClassLoader());

    @BeforeAll
    public static void setup(){
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void test_bundle_lookup(){
        WrappedBundle bundle;

        // Checks the existence of the French bundle
        bundle = provider.getBundle(Locale.FRENCH);
        assertNotNull(bundle);
        assertEquals(Locale.FRENCH, bundle.getLocale());

        // Checks if it fallbacks to the French bundle
        bundle = provider.getBundle(Locale.FRANCE);
        assertNotNull(bundle);
        assertEquals(Locale.FRENCH, bundle.getLocale());

        // Checks if it fallbacks to the Root bundle
        bundle = provider.getBundle(Locale.CHINA);
        assertNotNull(bundle);
        assertEquals(Locale.ROOT, bundle.getLocale());
    }

    @Test
    public void test_bundle_strings(){
        final var french = provider.getBundle(Locale.FRENCH);
        assertEquals(french.get("arrival"), "Bonjour");
        assertEquals(french.get("departure"), "Goodbye");
        assertEquals(french.get("comeback"), "???comeback???");

        final var root = provider.getBundle(Locale.ROOT);
        assertEquals(root.get("arrival"), "Hello");
        assertEquals(root.get("departure"), "Goodbye");
        assertEquals(root.get("comeback"), "???comeback???");

        assertNull(root.getOrNull("comeback"));
        assertThrows(MissingResourceException.class, () -> root.getNonNull("comeback"));
    }

    @Test
    public void test_throw_on_bad_bundle_basename(){
        final BundleProvider bad = l -> WrappedBundle.of("nonexistent/test", l, getClass().getClassLoader());
        assertThrows(MissingResourceException.class, () -> bad.getBundle(Locale.ROOT));
    }
}
