package fr.xpdustry.distributor.plugin;

import fr.xpdustry.distributor.util.*;

import rhino.*;

import java.util.*;


public class ScriptManager{
    private final ResourceLoader loader = new ResourceLoader(getClass().getClassLoader());
    private final SortedMap<String, Script> scripts = new TreeMap<>();
    private final SortedMap<String, Scriptable> sessions = new TreeMap<>();

    public Script addScript(String name, Script script){
        return scripts.put(name, script);
    }

    public Script getScript(String name){
        return scripts.get(name);
    }

    public Script removeScript(String name){
        return scripts.remove(name);
    }

    public Scriptable addSession(String name, Scriptable session){
        return sessions.put(name, session);
    }

    public Scriptable getSession(String name){
        return sessions.get(name);
    }

    public Scriptable removeSession(String name){
        return sessions.remove(name);
    }

    public SortedMap<String, Script> getScripts(){
        return new TreeMap<>(scripts);
    }

    public SortedMap<String, Scriptable> getSessions(){
        return new TreeMap<>(sessions);
    }

    public Collection<String> getScriptList(){
        return scripts.keySet();
    }

    public Collection<String> getSessionList(){
        return sessions.keySet();
    }
}
