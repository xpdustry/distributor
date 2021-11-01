package fr.xpdustry.distributor.command.runner;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;

import org.jetbrains.annotations.*;


public interface MindustryRunnerFactory{
    MindustryCommandRunner makeRunner(@NotNull MindustryCommand<Playerc> command);
}
