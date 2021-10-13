package fr.xpdustry.distributor.security;


public interface Permission{
    default long getAccess(){
        return 0L;
    }
}
