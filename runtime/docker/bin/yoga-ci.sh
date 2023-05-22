#!/bin/bash
# Provided by JMMC
# May 2023 : GM - creation
# Build a docker command line wrapper to run yoga/LITpro using shell wrapper

# log in /tmp
# echo "$*" >> /tmp/yoga.log

# Warning: setting/changing user throws yorick/pdf : ERROR (hcps) Segmentation violation interrupt (SIGSEGV) 
# docker run --rm -v /tmp:/tmp -u $(id -u ${USER}):$(id -g ${USER}) oiservices-litpro bin/yoga.sh $*

docker run --rm -v /tmp:/tmp oiservices-litpro bin/yoga.sh $*


