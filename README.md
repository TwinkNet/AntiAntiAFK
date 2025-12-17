# Anti-AntiAFK
A Minecraft server (Folia) plugin designed to combat the use of simple Anti-AFK modules on cheat utility clients.

## Why?
You may need this for your server if you specifically allow the use of these utility clients on your server, but find it problematic if you have an astonishingly high number of AFK bots on the server. These AFK bots, who exist solely to spam chat, AutoFish, and operate mob farms may occupy valuable player slots and consume server resources that could otherwise be used for live players.

### Ye olde generic Anti-AFK plugin might not be enough...
Modern AFK bots have a lot of tricks up their sleeves to not get kicked by Anti-AFK plugins. On servers with the most basic Anti-AFK checks, the client can get away with simply punching, crouching, and jumping to reset the AFK timer. On servers with more complex Anti-AFK plugins, the client may be able to get away with walking in circles, or breaking and placing a block over and over to avoid being kicked.

### So, how is Anti-AntiAFK different?
This plugin starts a player that has just connected off with a configurable timer. This timer decreases as time progresses. When the  timer reaches 0, the player is kicked for inactivity. The player's actions are weighted and measured to extend this timer by a customisable amount. In theory, if a live player is player, they should be easily be able to keep this timer from ever reaching 0 during their session. A client performing repeated actions should not be able to extend the timer faster than the timer counts down, therefore they will eventaully be kicked for inactivity. The goal is to make it so that a client would have to jeopardise the safety of the player to stay connected to the server for very long periods of the time. This would discourage a player from staying AFK.  

#  Downloads
None at the moment this is still in development... Keep an eye out.