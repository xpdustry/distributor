/*
 * A very simple script to add auto-pause to your server.
 */

var counter = 0

Events.on(PlayerJoin, e => {
    if (++counter === 1 && Vars.state.serverPaused) {
        Vars.state.serverPaused = false;
        info("AutoPause unpaused the server.")
    }
})

Events.on(PlayerLeave, e => {
    if (--counter === 0) {
        Vars.state.serverPaused = true;
        info("AutoPause paused the server.")
    }
})
