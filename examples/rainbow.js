/*
 * This script creates an admin command that makes every player fart a rainbow smoke :^)
 */

const rainbow = extend(Timer.Task, {
    effects: Seq.with(Fx.mine, Fx.mineBig, Fx.mineHuge),
    r: 255, g: 0, b: 0,

    next(){
        if(this.r > 0 && this.b === 0){this.r--; this.g++;}
        if(this.g > 0 && this.r === 0){this.g--; this.b++;}
        if(this.b > 0 && this.g === 0){this.r++; this.b--;}
        return new Color(this.r / 255, this.g / 255, this.b / 255)
    },

    run(){
        Groups.player.each(p => {
            let color = this.next()
            Call.effect(this.effects.random(), p.x(), p.y(), 0, color)
        });
    }
});

const registry = new CommandRegistry();

const command = registry.register(registry.builder("rainbow")
    .validator(Commands.DEFAULT_ADMIN_VALIDATOR)
    .description("Make everyone fart a rainbow.")
    .parameter(BooleanParameter.of("status", Commands.EXTENDED_BOOLEAN_PARSER))
    .runner(ctx => {
        if(ctx.get("status")){
            if(!rainbow.isScheduled()) Timer.schedule(rainbow, 0, 0.1);
            ctx.getCaller().sendMessage("You enabled the rainbow fart.")
        }else{
            if(rainbow.isScheduled()) rainbow.cancel()
            ctx.getCaller().sendMessage("You disabled the rainbow fart.")
        }
    })
)

registry.export(Commands.getClientCommands())
