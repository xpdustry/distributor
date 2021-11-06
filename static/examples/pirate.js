/*
 * https://github.com/BasedUser/PirateBrazilifier
 * Best script ever...
 */

const pirates = Seq.with(
    "VALVE",
    "tuttop",
    "CODEX",
    "IGGGAMES",
    "IgruhaOrg",
    "FreeTP.Org"
);

Events.on(PlayerConnect, e => {
    if(pirates.contains(e.player.name())){
        e.player.con().kick(
            "Mindustry is free on [royal]https://anuke.itch.io/mindustry[]\n" +
            "[red]Go get a legit copy of the game before coming in this server.");
    }
});
