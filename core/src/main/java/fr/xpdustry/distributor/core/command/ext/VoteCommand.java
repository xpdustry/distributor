package fr.xpdustry.distributor.core.command.ext;

import arc.struct.*;
import arc.util.*;

import fr.xpdustry.distributor.core.util.*;
import mindustry.game.EventType.*;

import fr.xpdustry.distributor.core.command.*;
import fr.xpdustry.distributor.core.event.*;

public class VoteCommand<T> extends CommandContainer<T>{
    public final Interval timer;
    public final Watcher<?> updater;

    protected final ObjectSet<T> votes = new ObjectSet<>();
    protected float voteDuration = 60 * Time.toSeconds;
    protected float voteCooldown = 60 * Time.toSeconds;
    protected float voteTimer = voteDuration;

    public VoteCommand(String name, String parameterText, String description, Object updateEvent){
        super(name, parameterText, description);

        timer = new Interval();
        updater = new Watcher<>(updateEvent, e -> {
            if(timer.get(voteTimer)){
                // TODO stuff
            }
        });
    }

    public void reset(){
        voteTimer = voteDuration;
        timer.reset(0, 0);
    }

    public void vote(String[] args, T type){
        // TODO...
    }
}
