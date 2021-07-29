package fr.xpdustry.distributor.util;


public interface Initializable{
    void init();

    default int priority(){
        return 0;
    }
}
