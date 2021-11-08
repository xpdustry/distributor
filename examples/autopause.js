/*
 * A simple script to add auto-pause to your server.
 */

// FIXME the autopause don't pause the server, it might to do something with the lastPauseStatus

var active = true
var lastPauseStatus = Vars.state.serverPaused

Events.on(PlayerJoin, e => {
    if(active && Vars.state.serverPaused != lastPauseStatus){
        Vars.state.serverPaused = lastPauseStatus;
        info("AutoPause unpaused the server.")
    }
})

Events.on(PlayerLeave, e => {
    if(active && Groups.player.size() == 0){
        lastPauseStatus = Vars.state.serverPaused
        Vars.state.serverPaused = true;
        info("AutoPause paused the server.")
    }
})


const registry = new CommandRegistry()

registry.register(registry.builder("auto-pause")
    .description("Automatically pause the game while no one is playing.")
    .parameter(BooleanParameter.of("status", Commands.EXTENDED_BOOLEAN_PARSER).optional())
    .runner(ctx => {
        if(ctx.getArgumentSize() == 0){
            info("AutoPause is currently @.", active ? "enabled" : "disabled")
        }else{
            active = ctx.get("status")
            info("AutoPause is now @.", active ? "enabled" : "disabled")
        }
    })
)

registry.export(Commands.getServerCommands())
