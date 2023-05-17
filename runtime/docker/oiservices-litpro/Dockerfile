# build from latest tagged oiservice image
FROM oiservices

# build as root
USER root


# Enable software installs (1=enable):
ARG DO_LITPRO=1

###################################
#
#  LITpro
#
ENV LITPRO_VERSION="V.TBD"

RUN set -eux ; \
    if [ "$DO_LITPRO" -eq "1" ] ; then \
    BUILD_DATE=`date -Is` && \
#BP    BUILD_PKG="git cvs" && \
# install LITPRO
    echo "install LITPRO ${LITPRO_VERSION} from local clone" && \
    echo "${LITPRO_VERSION}-$BUILD_DATE" > /opt/LITPRO_VERSION && \
    apt-get update && \
    apt-get install -y --no-install-recommends xsltproc libxml2-utils ghostscript graphicsmagick-imagemagick-compat && \
#BP    apt-get purge --auto-remove -y $BUILD_PKG && \
    rm -rf /var/lib/apt/lists/* ; \
    fi

# TODO replace next lines by associated one that retrieve material from git  or any other public repos
ADD LITproWrapper /opt/LITproWrapper
WORKDIR /opt/LITproWrapper
RUN chmod 777 log/yoga.log

# revert to previous user
USER tomcat
