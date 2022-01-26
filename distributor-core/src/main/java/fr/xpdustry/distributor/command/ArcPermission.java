package fr.xpdustry.distributor.command;

import cloud.commandframework.permission.*;


/**
 * Global class for making command permission usage easier.
 */
public final class ArcPermission{
    private ArcPermission(){
    }

    public static final CommandPermission ADMIN = Permission.of("distributor:admin");
}
