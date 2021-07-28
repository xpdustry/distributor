package fr.xpdustry.distributor.core.admin;

public interface Permission{
    default int getAccessLevel(){
        return 0;
    }
}
