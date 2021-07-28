package fr.xpdustry.distributor.core.util;


public interface Initializable{
    void init();

    default int priority(){
        return 0;
    }
}
