package fr.xpdustry.distributor.string;

public enum MessageIntent{
    /** This intent requires the sender to NOT apply special formatting */
    NONE,
    /** This intent is for debugging messages */
    DEBUG,
    /** This intent is for any normal message */
    INFO,
    /** This intent is for errors and warnings */
    ERROR
}
