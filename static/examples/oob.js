/*
 * A simple script to keep pvp players from going out of the map bounds,
 * to avoid separation walls for example. The damage are based on the maximum health.
 */

const oobx = x => ((x < 0) ? -x : (x < Vars.world.width() ? 0 : (x - Vars.world.width())));
const ooby = y => ((y < 0) ? -y : (y < Vars.world.height() ? 0 : (y - Vars.world.height())));

const oobTimer = new Interval();

Events.run(Trigger.update, () => {
    if(Vars.state.rules.pvp && oobTimer.get(Time.toSeconds / 2)){
        Groups.player.each(p => {
            let distance = Math.hypot(oobx(p.tileX()), ooby(p.tileY()));
            if(distance > 0){
                Call.announce(p.con(), "[red]You are out of the map bounds.\nYou will loose health!")
                p.unit().damage(p.unit().maxHealth() * (distance / 100));
            }
        })
    }
});
