package fr.xpdustry.distributor.util;

import arc.util.Log.*;

import org.checkerframework.checker.nullness.qual.*;


public class TestLogHandler implements LogHandler{
    private @Nullable LogLevel lastLevel = null;
    private @Nullable String lastText = null;

    @Override public void log(LogLevel level, String text){
        lastLevel = level;
        lastText = text;
    }

    public @Nullable LogLevel getLastLevel(){
        return lastLevel;
    }

    public @Nullable String getLastText(){
        return lastText;
    }
}
