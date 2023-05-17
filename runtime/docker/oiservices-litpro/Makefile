# Build a docker image for oiservices wrapping
# Building requires :
#  -
# look at the Dockerfile for main building operations
#

include ../oiservices/common.mk


NAME=oiservices-litpro

all:	build tag_latest

# call force-build to start again building process from the beginning
force-build:	Dockerfile
	docker build --no-cache=true -t $(NAME):$(VERSION) --rm .

build:	Dockerfile
	echo $(TAR_FILE)
	docker build -t $(NAME):$(VERSION) --build-arg OMP_THREAD_LIMIT=$(OMP_THREAD_LIMIT) --build-arg MAKEJ_NTHREADS=$(NTHREADS) --rm .

tag_latest:
	docker tag $(NAME):$(VERSION) $(NAME):latest

run:
	docker rm $(NAME) || true
	docker run -it --name $(NAME) --memory 8g --memory-swappiness 0 -p 8080:8080 $(NAME):$(VERSION)

shell:
	docker exec -it $(NAME) /bin/bash

publish: 
	docker login gricad-registry.univ-grenoble-alpes.fr
	docker tag $(NAME):$(VERSION)  gricad-registry.univ-grenoble-alpes.fr/osug/jmmc/vltischools/vltischool-2023/$(NAME):$(VERSION) 
	docker push gricad-registry.univ-grenoble-alpes.fr/osug/jmmc/vltischools/vltischool-2023/$(NAME):$(VERSION) 

clean:
	rm -f ../../../target/OImaging-uws.war ;\
	rm -rf OImaging-uws.war $(TAR_FILE)

