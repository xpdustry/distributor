package fr.xpdustry.distributor.security;


public interface Permission{
    default int getAccessLevel(){
        return 0;
    }
}
