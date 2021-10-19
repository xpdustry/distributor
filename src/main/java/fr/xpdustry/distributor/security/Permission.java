package fr.xpdustry.distributor.security;

import java.util.*;


public interface Permission<E extends Enum<E>>{
    EnumSet<E> getPermissions();

    // TODO find a better way to test permissions
    boolean hasPermissions(E... permissions);
}
