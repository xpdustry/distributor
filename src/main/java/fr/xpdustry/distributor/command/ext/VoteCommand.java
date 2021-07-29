package fr.xpdustry.distributor.command.ext;

import arc.struct.*;
import arc.util.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.event.*;

// NOTE poor attempt of doing a proper vote command, imma focus on the script loader

public abstract class VoteCommand<T> extends Command<T>{
    protected final Interval cooldown = new Interval();
    protected float voteDuration = 60 * Time.toSeconds;
    protected float voteCooldown = 60 * Time.toSeconds;

    private final int maxSessions;
    private final Seq<VoteSession<?>> sessions = new Seq<>(false);

    @SuppressWarnings("unchecked")
    public VoteCommand(String name, String parameterText, String description, int maxSessions, Object updateEvent){
        super(name, parameterText, description, (CommandRunner<T>) CommandRunner.voidRunner);

        this.maxSessions = maxSessions;

        this.runner = (args, type) -> {
            if(isReady()){
                sessions.add(begin(args, type));
            }else{
                print("You can't vote for now, still @ ", type);
            }
        };

        PostMan.on(VoteSession.class, session -> {

        });
    }

    public boolean isReady(){
        return (maxSessions != -1 || sessions.size < maxSessions) && cooldown.get(voteCooldown);
    }

    public abstract VoteSession<T> begin(String[] args, T type);

    public abstract void action();

    /*
    public void reset(){
        voteTimer = voteDuration;
        timer.reset(0, 0);
    }
     */

    public void vote(String[] args, T type){
        // TODO...
    }

    protected abstract void computeVote(String[] args, T type);


    public static class VoteSession<T>{
        public int votes;
        private final Watcher<?> updater;
        private final Interval timer = new Interval();
        private final ObjectSet<T> voters = new ObjectSet<>();

        public VoteSession(float voteDuration, Object updateEvent){
            updater = new Watcher<>(updateEvent, e -> {
                if(timer.get(voteDuration)){
                    PostMan.fire(this);
                }
            });
        }
    }
}
