# L2j Server Docker image running on Alpine Linux with docker-compose

[![Docker Pulls](https://img.shields.io/docker/pulls/l2jserver/l2j-server-docker.svg?style=for-the-badge&logo=docker)](https://hub.docker.com/r/l2jserver/l2j-server-docker/)

[![Alpine Version](https://img.shields.io/badge/Alpine%20version-v3.12.0-green.svg?style=for-the-badge&logo=alpine-linux)](https://alpinelinux.org/)
[![MariaDB Version](https://img.shields.io/badge/Mariadb%20version-v10.4.13-green.svg?style=for-the-badge&logo=mariadb)](https://mariadb.org/)


This docker-compose is using [(yobasystems/alpine-mariadb)](https://hub.docker.com/r/yobasystems/alpine-mariadb/) based on the minimal [Alpine Linux](https://alpinelinux.org/) with [MariaDB v10.4.13](https://mariadb.org/)


## Requirements 


### Windows

[Install Docker Desktop for Windows](https://hub.docker.com/editions/community/docker-ce-desktop-windows)

### Linux

[Install docker for Centos](https://docs.docker.com/engine/install/centos/)

[Install docker for Debian](https://docs.docker.com/engine/install/debian/)

Then start the linux service

`systemctl status docker.service`


### Mac

[Install Docker Desktop for Mac](https://hub.docker.com/editions/community/docker-ce-desktop-mac)

macOS must be version 10.13 or newer, i.e. High Sierra (10.13), Mojave (10.14) or Catalina (10.15).


## Use docker-compose.yml

After the docker installation run the following command in any Linux / Windows terminal into the l2j-server-docker folder to get your local server running

`docker-compose -f "docker-compose.yml" up -d`

Wait until the server is fully deployed and connected to 127.0.0.1 and you are ready to go.


### Logging

If you want to check the logs while the server is starting/running use a terminal with the command

`docker logs l2j-server-docker --tail 50 -f` 


### Attaching a shell to check the container files manually

Attach a shell to navigate around the server container files

`docker exec -it l2j-server-docker /bin/sh -c "[ -e /bin/bash ] && /bin/bash || /bin/sh"`


### Configurable environment variables

The default values can be modified in the docker-compose.yml file. 

The variables are optional, so they aren't in the default docker-compose.yml setup, but you can add them by yourself.

- SERVER_IP : Your private or public server IP  (default: "127.0.0.1")
- JAVA_XMS : Initial memory allocation pool (default: "512m")
- JAVA_XMX : Maximum memory allocation pool (default: "2g")
- RATE_XP : Rates for XP Gain (default: "1")
- RATE_SP : Rates for SP Gain (default: "1")
- ADMIN_RIGHTS : Everyone has Admin rights (default: "False")
- FORCE_GEODATA: Forces geodata (default: "False")
- COORD_SYNC: Coordinates sync configuration (default: "-1")
- HELLBOUND_ACCESS: Hellbound without Quest (default: "False")
- WEIGHT_LIMIT: Increases the weight limit ratio (default: "1")
- TVT_ENABLED: Enables the Team Vs Team PvP Event (default: "False")
- SAVE_GM_SPAWN_ON_CUSTOM: Save any admin spawn (default: "False")
- CUSTOM_SPAWNLIST_TABLE: Enables the custom spawnlist folder (default: "False")
- CUSTOM_NPC_DATA:  Enables the custom NPC data (default: "False")
- CUSTOM_TELEPORT_TABLE: Enables the custom teleport table (default: "False")
- CUSTOM_NPC_BUFFER_TABLES: Enables the NPC buffer scheme tables (default: "False")
- CUSTOM_SKILLS_LOAD: Enables the custom skills data (default: "False")
- CUSTOM_ITEMS_LOAD:  Enables the custom items data (default: "False")
- CUSTOM_MULTISELL_LOAD: Enables the multisell data (default: "False")
- CUSTOM_BUYLIST_LOAD: Enables the buylist data (default: "False")
- DATABASE_USER : Database user that has admin priviledges on MariaDB server (default: "root")
- DATABASE_PASS : Database password for user with admin priviledges (default: "root")
- LAN_ADDRESS : External network address, which client is going to be using to connect to server (default: "10.0.0.0")
- LAN_SUBNET : External network subnet, used by server to communicate to client for connection (default: "10.0.0.0/8")
- QUEST_MULTIPLIER_XP : Quest reward multiplier for XP (default: "1")
- QUEST_MULTIPLIER_SP : Quest reward multiplier for SP (default: "1")
- QUEST_MULTIPLIER_REWARD : Quest reward multiplier for any item rewards (default: "1")
- VITALITY_SYSTEM : Enable Vitality system (default: "True")
- AUTO_LEARN_SKILLS : Class skills will be delivered upon level up and login (default: "False")
- MAX_FREIGHT_SLOTS : Maximum items that can be placed in Freight (default: "200")
- DWARF_RECIPE_LIMIT : Limits for Dwarf class recipes (default: "50")
- COMM_RECIPE_LIMIT : Common recipe limit for all classes (default: "50")
- CRAFTING_SPEED_MULTIPLIER : The higher the number, the more time the crafting process takes. (default: "1")
- FREE_TELEPORTING : Enable free teleporting around the world (default: "False")
- STARTING_ADENA : Amount of Adena that a new character possesses (default: "0")
- STARTING_LEVEL : Starting level of a new character (default: "1")
- STARTING_SP : Starting amount of SP assigned to a new character (default: "0")
- ALLOW_MANOR : Enable Manor System (default: "True")
- SERVER_DEBUG: Enable server debugging, not meant for LIVE server (default: "False")
- MAX_ONLINE_USERS: How many players are allowed to play simultaneously on your server (default: "500")
- MAX_WAREHOUSE_SLOTS_DWARF: Dwarf character's warehouse capacity. This must be LESS then 300 or the client will crash. (default: "120")
- MAX_WAREHOUSE_SLOTS_NO_DWARF: Non-Dwarf character's warehouse capacity. This must be LESS then 300 or the client will crash. (default: "100")
- MAX_WAREHOUSE_SLOTS_CLAN: Clan specific warehouse capacity. This must be LESS then 300 or the client will crash. (default: "200")
- PET_XP_RATE: XP multiplier for leveling pets (default: "1")
- ITEM_SPOIL_MULTIPLIER : Multiplies the amount of items looted from monster when a skill like Sweeper(Spoil) is used (default: "1")
- ITEM_DROP_MULTIPLIER : Multiplies the amount of items dropped from monster on ground when it dies (default: "1")
- BUFFER_SERVICE: Enable/Disable BufferService (default: "False"). Requires CUSTOM_NPC_DATA enabled. 
- BUFFER_SERVICE_COOLDOWN: How long players have to wait to heal again in seconds (default: "60").
- BUFFER_SERVICE_MAX_LISTS: How many schemes players can have (default: "5").
- BUFFER_SERVICE_VOICED: Enable/Disable voiced buffer (default: "False").
- BUFFER_SERVICE_VOICED_COMMAND: The voiced command for the voiced buffer (default: "bufferservice").
- BUFFER_SERVICE_VOICED_NAME: The name of the voiced buffer (default: "Voiced").
- BUFFER_SERVICE_VOICED_REQUIRED_ITEM: Item id required to use the voiced buffer (default: "0").
- BUFFER_DEBUG: Enable/Disable debug messages (default: "False").
- DISCORD_BOT_ENABLE: Enable/Disable Discord bot (default: "False"). 
- DISCORD_BOT_PREFIX: Use the Prefix for your Discord bot to listen the bot commands (default: "//"). 
- DISCORD_BOT_TOKEN: A Token from Discord (default: "NzY3Mzg5NjE").
- DISCORD_BOT_CHANNEL_ID: A Channel Id from Discord (default: "732358666681843752").

### Connecting to Discord

Use the link below to create your discord bot and there you will find the info needed for (DiscordBotToken =)

https://discordapp.com/developers/applications/

Create a discord server and activate Developer mode. (User Settings->Appearance->Developer Mode.)

Developer Mode exposes context menu items helpful for people writing bot using Discord API.

https://discordpy.readthedocs.io/en/latest/discord.html - A guide how to create a Bot account.

See also https://discordpy.readthedocs.io/en/latest/discord.html#inviting-your-bot - on how to invite your bot.

#### Getting the TOKEN

A Discord Bot Token is a short phrase (represented as a jumble of letters and numbers) that acts as a key to control a Discord Bot.

Tokens are used inside bot code to send commands back and forth to the API, which in turn controls bot actions.

WARNING Never share your Discord Bot Token with anyone. WARNING.

#### Getting the Channel Id

Use the Channel ID from your Discord Server/Guild that you want the server logs to be posted.

Server admins hide this channel from public view. Developer Mode must be ON. Right click the channel to get the id. (Copy ID)


### Managing the cluster with docker-compose.yml

Deploy the containers cluster (the first time)

`docker-compose up -d`

Stop the cluster

`docker-compose stop`

Start the cluster

`docker-compose start`

Removes the cluster containers (WARNING: `down` will removes all your data)

`docker-compose down`


To have a persistent server use stop/start. 

It's always recommended for live servers to stop the gameserver in-game before stop the whole cluster.


## Customize your own Docker image

If you want recreate the images yourself checkout the following Dockerfiles repositories

[yobasystems/alpine-mariadb](https://github.com/yobasystems/alpine-mariadb)

[l2jserver/l2j-server-docker](https://bitbucket.org/l2jserver/l2j-server-docker)


Customize the l2j-server image and rebuild it with the following command

`docker build -t l2jserver/l2j-server-docker:latest .`

Then you will ready to use the custom image with your docker-compose file.

## Attach a bash shell to the server

A shell for l2jserver container

`docker exec -it l2jserver /bin/bash`

A shell for mariadb container

`docker exec -it mariadb /bin/bash`

## Troubleshooting

Use `down` to stop the cluster but if you are experiencing problems execute the following commands to restart all the volumes and containers

`docker system prune -a`

`docker volume prune`


# License

L2J Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Please read the complete [LICENSE](https://bitbucket.org/l2jserver/l2j-server-docker/src/master/LICENSE.md)