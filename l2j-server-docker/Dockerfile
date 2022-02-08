# Copyright 2004-2020 L2J Server
# L2J Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
# L2J Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
# You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.

FROM openjdk:15-alpine

LABEL maintainer="L2JServer" \
      version="2.6.2.0" \
      website="l2jserver.com"

COPY entrypoint.sh /entrypoint.sh

ARG branch_gs=develop
ARG branch_dp=develop

RUN apk update \ 
    && apk --no-cache add maven mariadb-client unzip git \
    && mkdir -p /opt/l2j/server && mkdir -p /opt/l2j/target && cd /opt/l2j/target/ \
    && git clone --branch master --single-branch https://git@bitbucket.org/l2jserver/l2j-server-cli.git cli \
    && git clone --branch master --single-branch https://git@bitbucket.org/l2jserver/l2j-server-login.git login \
    && git clone --branch $branch_gs --single-branch https://git@bitbucket.org/l2jserver/l2j-server-game.git game \
    && git clone --branch $branch_dp --single-branch https://git@bitbucket.org/l2jserver/l2j-server-datapack.git datapack \
    && cd /opt/l2j/target/cli && chmod 755 mvnw && ./mvnw install \
    && cd /opt/l2j/target/login && chmod 755 mvnw && ./mvnw install \
    && cd /opt/l2j/target/game && chmod 755 mvnw && ./mvnw install \
    && cd /opt/l2j/target/datapack && chmod 755 mvnw && ./mvnw install \
    && unzip /opt/l2j/target/cli/target/*.zip -d /opt/l2j/server/cli \
    && unzip /opt/l2j/target/login/target/*.zip -d /opt/l2j/server/login \
    && unzip /opt/l2j/target/game/target/*.zip -d /opt/l2j/server/game \
    && unzip /opt/l2j/target/datapack/target/*.zip -d /opt/l2j/server/game \
    && rm -rf /opt/l2j/target/ && apk del maven git \
    && chmod +x /opt/l2j/server/cli/*.sh /opt/l2j/server/game/*.sh /opt/l2j/server/login/*.sh /entrypoint.sh


WORKDIR /opt/l2j/server

EXPOSE 7777 2106

ENTRYPOINT [ "/entrypoint.sh" ]