package fr.xpdustry.distributor.script;

import rhino.*;

import java.util.*;

public class SessionManager{
    private final Map<String, Scriptable> sessions = new HashMap<>();

    public Scriptable getSession(String name){
        return sessions.get(name);
    }

    public Scriptable removeSession(String name){
        return sessions.remove(name);
    }
}
