package fr.xpdustry.distributor.command.caption;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


public class ArcCaptionRegistry extends SimpleCaptionRegistry<ArcCommandSender>{
    public ArcCaptionRegistry(){
        super();
    }

    public void registerMessageFactory(@NonNull Caption caption, @NonNull BundleProvider provider){
        registerMessageFactory(caption, new BundleProviderMessageFactory(provider));
    }

    public static final class BundleProviderMessageFactory implements BiFunction<Caption, ArcCommandSender, String>{
        private final @NonNull BundleProvider provider;

        private BundleProviderMessageFactory(@NonNull BundleProvider provider){
            this.provider = provider;
        }

        @Override public @NonNull String apply(@NonNull Caption caption, @NonNull ArcCommandSender sender){
            return provider.getBundle(sender).get(caption);
        }
    }
}
