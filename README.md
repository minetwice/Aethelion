# EmoteEngine

A powerful Minecraft plugin that adds custom emotes and animations with body scaling support.

## Features
- ✅ Blockbench animation support (.bbmodel)
- ✅ Body part scaling (head, arms, legs, etc.)
- ✅ Resource pack auto-generation
- ✅ Paper 1.17-1.21.11 support
- ✅ Spigot compatible
- ✅ Developer API
- ✅ No client mods required

## Commands
- `/emote list` - Show all emotes
- `/emote play <id>` - Play an emote
- `/emote stop` - Stop all emotes
- `/emote scale <bone> <value>` - Scale a body part
- `/emote reset [bone]` - Reset scale
- `/emote reload` - Reload animations (admin)

## Developer API
```java
EmoteAPI api = (EmoteAPI) Bukkit.getPluginManager().getPlugin("EmoteEngine");
api.playEmote(player, "dance");
api.setScale(player, "head", 2.0f);
```

## Building
```bash
./gradlew shadowJar
```

The compiled jar will be in `build/libs/`