package fr.xpdustry.distributor.string;

import fr.xpdustry.distributor.localization.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class MessageBroadcaster implements TranslatingMessageReceiver{
    private final Translator translator;
    private final Collection<MessageReceiver> receivers;

    public MessageBroadcaster(Translator translator, Collection<MessageReceiver> receivers){
        this.translator = translator;
        this.receivers = receivers;
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args){
        receivers.forEach(r -> r.sendMessage(intent, message, args));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars){
        receivers.forEach(r -> r.sendMessage(intent, message, vars));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull Caption caption, @NotNull CaptionVariable... vars){
        // TODO improve system to support locales correctly
        // final var translation = translator.translate(caption, )
        // receivers.forEach(r -> r.sendMessage(intent, translator));
    }
}
