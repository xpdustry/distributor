package fr.xpdustry.distributor.core.command;

import arc.util.*;
import mindustry.gen.*;


/**
 * The consumer interface used to run the commands.
 */
public interface CommandRunner{
    /** {@code voidRunner} is usually used for custom {@link Command} implementations or subclasses. */
    CommandRunner voidRunner = (args, player) -> {};

    void accept(String[] args, @Nullable Player player);
}
