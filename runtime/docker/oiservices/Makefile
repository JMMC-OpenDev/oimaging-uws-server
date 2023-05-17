# Build a docker image for oiservices wrapping
# Building requires :
#  - bsmem sources
#  - OImaging-uws war
# look at the Dockerfile for main building operations
#
# oiservices can run the service (UWS server) without args
#   or run a binary inside the container

include ../oiservices/common.mk

NAME=oiservices


# hard-coded as docker image is not prepared on server VM
CPU_COUNT:=4
OMP_THREAD_LIMIT:=2

NTHREADS := $(shell getconf _NPROCESSORS_ONLN) # find nthreads for make -j

# $(shell grep -c '^processor' /proc/cpuinfo)

all:	build tag_latest

# call force-build to start again building process from the beginning
force-build:	Dockerfile OImaging-uws.war
	docker build --no-cache=true -t $(NAME):$(VERSION) --rm .

build:	Dockerfile OImaging-uws.war
	docker build -t $(NAME):$(VERSION) --build-arg OMP_THREAD_LIMIT=$(OMP_THREAD_LIMIT) --build-arg MAKEJ_NTHREADS=$(NTHREADS) --rm .

tag_latest:
	docker tag $(NAME):$(VERSION) $(NAME):latest

OImaging-uws.war: ../../../target/OImaging-uws.war
	cp $? .

# Set Max running job in war file
../../../target/OImaging-uws.war:
	cd ../../.. ;\
	echo "Setting MAX_RUNNING_JOBS_VALUE=$(CPU_COUNT) in src/main/webapp/WEB-INF/web.xml" ;\
	sed -i "s/MAX_RUNNING_JOBS_VALUE/$(CPU_COUNT)/g" ./src/main/webapp/WEB-INF/web.xml ;\
	mvn clean install ;\
	cd -

run:
	docker rm $(NAME) || true
	docker run -it --name $(NAME) --memory 8g --memory-swappiness 0 -p 8080:8080 $(NAME):$(VERSION)

shell:
	docker exec -it $(NAME) /bin/bash

$(TAR_FILE):
	docker save -o $(TAR_FILE) $(NAME):$(VERSION)

# This image snapshot is used in the ansible jmmc-oimaging-uws role (look into vars so that the versions match)
publish: $(TAR_FILE)
	scp $(TAR_FILE) apps-old.jmmc.fr.to:/data/docker/

exec-bsmem-ci-help:
	docker run -it $(NAME):$(VERSION) bsmem-ci -h

exec-wisard-ci-help:
	docker run -it $(NAME):$(VERSION) wisard-ci -h

clean:
	rm -f ../../../target/OImaging-uws.war ;\
	rm -rf OImaging-uws.war $(TAR_FILE)

