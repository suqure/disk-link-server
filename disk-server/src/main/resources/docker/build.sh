#!/bin/bash
DOCKER_IMG=@docker.image@
DOCKER_CONTAIN=@docker.container@
APP_PORT=@project.port@
HOST_PROT=@docker.host.port@
APP_LOG_DIR=@project.log@
HOST_LOG_DIR=@docker.host.log@
APP_DIR=@project.dir@
HOST_DIR=@docker.host.dir@
MAX_MEM=@docker.memory.limit@
docker stop ${DOCKER_CONTAIN}
docker rm  ${DOCKER_CONTAIN}
docker rmi  ${DOCKER_IMG}
docker build -t ${DOCKER_IMG} ./
docker run --name ${DOCKER_CONTAIN} --restart=always --privileged=true --net=host -m ${MAX_MEM} -p ${HOST_PROT}:${APP_PORT} -v ${HOST_LOG_DIR}:${APP_LOG_DIR} -v ${HOST_DIR}:${APP_DIR} -d ${DOCKER_IMG}
