# Exposes UWS services - Work In Progress
# https://hub.docker.com/_/tomcat/
#
FROM tomcat:10-jdk11-temurin-jammy

###################################
#
# Usual maintenance
#

# ALWAYS RUN apt-get update && apt-get install -y --no-install-recommends

# Running docker with ubuntu distribution
ENV DEBIAN_FRONTEND=noninteractive

RUN set -eux ; \
    apt-get install apt-transport-https -y && \
    apt-get update && apt-get upgrade -y && \
# Add vi, top, ps, vi:
    apt-get install -y --no-install-recommends procps vim wget && \
# Eliminate default web applications
    rm -rf ${CATALINA_HOME}/webapps/* && \
    rm -rf ${CATALINA_HOME}/server/webapps/* && \
# Define tomcat user & group
    addgroup --gid 900 tomcat && \
    adduser --system --uid 901 --gid 900 --home $CATALINA_HOME --shell /bin/bash --disabled-login tomcat && \
    chown -R tomcat:tomcat $CATALINA_HOME


# General GCC tuning:
# VMWare vCPU uses the broadwell arch (lower level requirement)

# For Production:
# -Ofast or -ffast-math disables NaN handling
# see https://stackoverflow.com/questions/27315941/gcc-ofast-complete-list-of-limitations
#ARG GCC_OPTS="-O3 -march=broadwell -mfpmath=sse -fomit-frame-pointer -fstrict-aliasing -DNDEBUG -pipe"

# LBO: use native to get max performance on local machine
ARG GCC_OPTS="-O3 -march=native -mfpmath=sse -fomit-frame-pointer -fstrict-aliasing -DNDEBUG -pipe"

ARG USE_VALGRIND=0
# For Valgrind:
#ARG GCC_OPTS="-Og -g -march=broadwell -mfpmath=sse -fstrict-aliasing -DNDEBUG -pipe"

# To debug builds:
ARG MAKE_OPTS="VERBOSE=1"
# To speedup make -j calls below (WISARD only)
ARG MAKEJ_NTHREADS=1


# Enable software installs (1=enable):
ARG DO_BSMEM=1
ARG DO_MIRA=1
ARG DO_SPARCO=1
ARG DO_WISARD=1

ARG MAKE_OPTS="VERBOSE=1"

ARG OMP_THREAD_LIMIT=1
# Limit threads for OpenMP:
ENV OMP_THREAD_LIMIT=${OMP_THREAD_LIMIT}

# Reduce memory footprint for tomcat (UWS):
ENV CATALINA_OPTS="-Xms64m -Xmx128m"


###################################
#
#  BSMEM
#
ENV BSMEM_CI_VERSION="v2.3.0"

RUN set -eux ; \
    if [ "$DO_BSMEM" -eq "1" ] ; then \
    BUILD_DATE=`date -Is` && \
# install oifitslib for bsmem after prerequisites
	BUILD_PKG="git cmake make gcc g++ gfortran libcfitsio-dev libglib2.0-dev libfftw3-dev libnfft3-dev bzip2" && \
    apt-get update && apt-get install -y --no-install-recommends $BUILD_PKG libglib2.0-bin && \
    cd /opt && git clone --depth 1 -b v2.6.2 https://github.com/jsy1001/oifitslib.git && cd oifitslib && \
    cd /opt/oifitslib/build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_FLAGS_RELEASE="$GCC_OPTS" -DCMAKE_CXX_FLAGS_RELEASE="$GCC_OPTS" .. && \
    make $MAKE_OPTS && make install && cd /opt && rm -rf /opt/oifitslib && \
# install bsmem
    echo "install BSMem ${BSMEM_CI_VERSION} from gitlab.com/jsy1001/bsmem" && \
    cd /opt/ && git clone --depth 1 -b ${BSMEM_CI_VERSION} https://gitlab.com/jsy1001/bsmem.git && \
    echo "${BSMEM_CI_VERSION}-$BUILD_DATE" > /opt/BSMEM_CI_VERSION && \
    apt-get install -y --no-install-recommends pgplot5 xterm libcfitsio9 libfftw3-single3 libfftw3-double3 libnfft3-single2 libnfft3-double2 && \
    cd /opt/bsmem*/build && cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_FLAGS_RELEASE="$GCC_OPTS" -DCMAKE_CXX_FLAGS_RELEASE="$GCC_OPTS" .. && \
    make $MAKE_OPTS && make install && cd /opt && rm -rf /opt/bsmem* && \
    apt-get purge --auto-remove -y $BUILD_PKG && rm -rf /var/lib/apt/lists/* ; \
    fi


###################################
#
#  WISARD-CI
#

# GDL: gnudatalanguage/gdl.git or bourgesl/gdl.git
ENV GDL_VERSION="gnudatalanguage/gdl.git" \
    WISARD_CI_VERSION="v3.2.4" \
    WISARD_DIR="/opt/wisard-ci" \
    ASTROLIB_DIR="/opt/astrolib"

RUN set -eux ; \
    if [ "$DO_WISARD" -eq "1" ] ; then \
    BUILD_DATE=`date -Is` && \
    BUILD_PKG="git cmake make gcc g++ libncurses5-dev zlib1g-dev libgsl-dev libplplot-dev libreadline-dev libcfitsio-dev libfftw3-dev" && \
    apt-get update && apt-get install -y --no-install-recommends $BUILD_PKG unzip libpng16-16 libncurses5 zlib1g libgsl2? libplplot17 libplplot-data libreadline8 libplplotcxx?? libcfitsio9 libfftw3-single3 libfftw3-double3 libtirpc3 && \
# Eigen install into /usr/local/include/eigen3/ (only header files)
    cd /opt && wget -q https://gitlab.com/libeigen/eigen/-/archive/3.3.9/eigen-3.3.9.zip && unzip -q eigen-*.zip && rm eigen-*.zip && \
    cd /opt/eigen-* && mkdir build && cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release .. && \
    make install && rm -rf /opt/eigen-* && \
# idl-astro
    cd /opt/ && git clone --depth 1 https://github.com/wlandsman/IDLAstro.git && \
    mkdir -p $ASTROLIB_DIR && cp IDLAstro/pro/* $ASTROLIB_DIR/ && rm -rf IDLAstro* && \
# Gdl
    cd /opt/ && git clone --depth 1 https://github.com/${GDL_VERSION} && \
# do not disable openmp, see OMP_THREAD_LIMIT env
    if [ "${OMP_THREAD_LIMIT}" -gt "1" ]; then USE_OPENMP="ON"; else USE_OPENMP="OFF"; fi && \
    export CMAKE_ARGS="-DOPENMP=${USE_OPENMP} -DEDITLINE=ON -DREADLINE=ON -DFFTW=ON -DEIGEN3=ON -DEIGEN3DIR=/usr/local/include/eigen3/ -DX11=OFF" && \
    export CMAKE_ARGS="$CMAKE_ARGS -DGRAPHICSMAGICK=OFF -DMAGICK=OFF -DWXWIDGETS=OFF -DTIFF=OFF -DNETCDF=OFF -DHDF=OFF -DHDF5=OFF -DPYTHON=OFF" && \
    export CMAKE_ARGS="$CMAKE_ARGS -DLIBPROJ=OFF -DSHAPELIB=OFF -DEXPAT=OFF -DUDUNITS2=OFF -DGRIB=OFF -DGLPK=OFF -DINTERACTIVE_GRAPHICS=OFF -DQHULL=OFF" && \
    cd /opt/gdl && mkdir build && cd build && \
    git submodule update --init && \
    cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_FLAGS_RELEASE="$GCC_OPTS" -DCMAKE_CXX_FLAGS_RELEASE="$GCC_OPTS" $CMAKE_ARGS .. && \
    make -j ${MAKEJ_NTHREADS} $MAKE_OPTS && make install && rm -rf /opt/gdl && rm -rf /usr/local/share/gnudatalanguage/resource/maps && \
    rm -rf /usr/local/include/eigen3 && \
    echo "install WISARD-CI ( version: ${WISARD_CI_VERSION} )" && \
# install wisard from github
    cd /opt/ && git clone --depth 1 -b ${WISARD_CI_VERSION} https://github.com/JMMC-OpenDev/wisard-ci.git && \
    echo "${WISARD_CI_VERSION}-$BUILD_DATE" > /opt/WISARD_CI_VERSION && \
    rm -rf $WISARD_DIR/inputdata && \
# install astrolib in GDL
    ln -s /opt/astrolib /usr/local/share/gnudatalanguage/lib && \
    apt-get purge --auto-remove -y $BUILD_PKG && rm -rf /var/lib/apt/lists/* ; \
    fi


###################################
#
#  MiRA
#

ENV MIRA_CI_VERSION="emmt/MiRA"

ARG IPY_VERSION="2.0.0"
ARG OPTIMPACKLEGACY_VERSION="1.4.3"
ARG YETI_VERSION="6.8.1"
ARG YLIB_VERSION="2.0.0"
ARG YOIFITS_VERSION="1.2.0"
ARG YNFFT_VERSION="1.0.4"

# install Yorick
# inspired from https://hub.docker.com/r/ferreol/yorick/~/dockerfile/
RUN set -eux ; \
    if [ "$DO_MIRA" -eq "1" ] ; then \
#RUN echo http://nl.alpinelinux.org/alpine/edge/testing >> /etc/apk/repositories && \
#    apk add --update --no-cache --virtual .build-deps make wget build-base zip && \
#    apk add --update --no-cache rlwrap && \
# rlwrap is only used in interpreter mode (skipped)
    BUILD_DATE=`date -Is` && \
    BUILD_PKG="make gcc g++ gfortran zip" && \
    apt-get update && apt-get install -y --no-install-recommends $BUILD_PKG unzip libnfft3-dev && \
    mkdir -p /opt && cd /opt && \
    wget -q https://github.com/dhmunro/yorick/archive/master.zip && \
    unzip -q master.zip && rm master.zip && cd yorick-master && \
    make install NO_XLIB=yes COPT_DEFAULT="$GCC_OPTS" && mv relocate /opt/yorick && \
    ln -s /opt/yorick/bin/yorick /usr/local/bin/yorick && \
    rm -rf /opt/yorick-master && \
#    apk del --virtual .build-deps && \
#    rm -rf /var/cache/apk/* /opt/yorick/doc/* && \
    touch /opt/yorick/yorick.commands && \
#
# Install MiRA and its dependencies.
# inspired from https://hub.docker.com/r/ferreol/yeti/~/dockerfile/
# with configure asked for install in /usr (/usr/lib is automatically found) + debian package renamed
#RUN echo http://nl.alpinelinux.org/alpine/edge/testing >> /etc/apk/repositories && \
#    apk add --update --no-cache --virtual .build-deps make wget build-base zip && \
#    apk add --update --no-cache fftw-dev gsctsl-dev && \
    mkdir -p /opt && cd /opt && \
# Install YLIB.
    wget -q "https://github.com/emmt/ylib/archive/v${YLIB_VERSION}.zip" && \
    unzip -q "v${YLIB_VERSION}.zip" && rm "v${YLIB_VERSION}.zip" && cd "ylib-${YLIB_VERSION}" && \
    ./configure && make install && cd /opt && rm -rf "ylib-${YLIB_VERSION}" && \
# Install Yeti.
    wget -q "https://github.com/emmt/Yeti/archive/v${YETI_VERSION}.zip" && \
    unzip -q "v${YETI_VERSION}.zip" && rm "v${YETI_VERSION}.zip" && cd "Yeti-${YETI_VERSION}" && \
    ./configure --with-fftw=no --with-regex=no --with-tiff=no && \cd
    make $MAKE_OPTS all && make install && cd /opt && rm -rf "Yeti-${YETI_VERSION}" && \
# Install IPY.
    wget -q "https://github.com/emmt/IPY/archive/v${IPY_VERSION}.zip" && \
    unzip -q "v${IPY_VERSION}.zip" && rm "v${IPY_VERSION}.zip" && cd "IPY-${IPY_VERSION}" && \
    ./configure --cflags="$GCC_OPTS" && make $MAKE_OPTS && make install && cd /opt && rm -rf "IPY-${IPY_VERSION}" && \
# Install OptimPackLegacy.
    wget -q "https://github.com/emmt/OptimPackLegacy/archive/v${OPTIMPACKLEGACY_VERSION}.zip" && \
    unzip -q "v${OPTIMPACKLEGACY_VERSION}.zip" && rm "v${OPTIMPACKLEGACY_VERSION}.zip" && cd "OptimPackLegacy-${OPTIMPACKLEGACY_VERSION}/yorick" && \
    ./configure --cflags="$GCC_OPTS" && make $MAKE_OPTS && make install && cd /opt && rm -rf "OptimPackLegacy-${OPTIMPACKLEGACY_VERSION}" && \
# Install YOIFITS.
    wget -q "https://github.com/emmt/YOIFITS/archive/v${YOIFITS_VERSION}.zip" && \
    unzip -q "v${YOIFITS_VERSION}.zip" && rm "v${YOIFITS_VERSION}.zip" && cd "YOIFITS-${YOIFITS_VERSION}" && \
    ./configure --cflags="$GCC_OPTS" && make $MAKE_OPTS && make install && cd /opt && rm -rf "YOIFITS-${YOIFITS_VERSION}" && \
# Install YNFFT.
    wget -q "https://github.com/emmt/ynfft/archive/v${YNFFT_VERSION}.zip" && \
    unzip -q "v${YNFFT_VERSION}.zip" && rm "v${YNFFT_VERSION}.zip" && cd "ynfft-${YNFFT_VERSION}" && \
    ./configure --cflags="$GCC_OPTS -pedantic -std=c99" && \
    make $MAKE_OPTS && make install && cd /opt && rm -rf "ynfft-${YNFFT_VERSION}" && \
# Install MiRA.
    wget -q https://github.com/${MIRA_CI_VERSION}/archive/master.zip && \
    echo "${MIRA_CI_VERSION}-$BUILD_DATE" > /opt/MIRA_CI_VERSION && \
    unzip master.zip && rm master.zip && cd MiRA-master/ && \
    ./configure --prefix=/usr/local && make $MAKE_OPTS install && \
    cd /opt && rm -rf MiRA-master && rm -rf /opt/yorick/doc && \
#    apk del .build-deps && \
#    rm -rf /var/cache/apk/* && rm -rf /usr/local/share/doc
    apt-get purge --auto-remove -y $BUILD_PKG && rm -rf /var/lib/apt/lists/* ; \
    fi


###################################
#
#  Sparco
#

ENV SPARCO_CI_VERSION="kluskaj/mira-sparco-multi"

# install Sparco plugin
# inspired from https://github.com/FerreolS/mira-sparco-multi/blob/master/docker/Dockerfile
RUN set -eux ; \
    if [ "$DO_SPARCO" -eq "1" ] ; then \
    BUILD_DATE=`date -Is` && \
# RUN echo http://nl.alpinelinux.org/alpine/edge/testing >> /etc/apk/repositories && \
#    apk add --update --no-cache --virtual .build-deps wget && \
    echo "${SPARCO_CI_VERSION}-$BUILD_DATE" > /opt/SPARCO_CI_VERSION && \
    cd /opt/yorick/i && wget https://raw.githubusercontent.com/${SPARCO_CI_VERSION}/master/mira2_plugin_sparcomulti.i ; \
#    apk del .build-deps  && \
#    rm -rf /var/cache/apk/* && rm -rf /usr/local/share/doc
    fi


###################################
#
#  Finalization
#

# Patches:
# none

# Enable fitsverify (1=enable):
ARG DO_FITS_VERIFY=0

# Install fitsverify (DEV)
RUN set -eux ; \
    echo "$DO_FITS_VERIFY" > /opt/FITS_VERIFY && \
    if [ "$DO_FITS_VERIFY" -eq "1" ] ; then \
    apt-get update && apt-get install -y --no-install-recommends fitsverify qfits-tools ; \
    fi


# Update custom wrappers:
ADD bsmem-ci.sh /usr/local/bin/bsmem-ci.sh
ADD wisard-ci.sh /usr/local/bin/wisard-ci
ADD mira-ci.sh /usr/local/bin/mira-ci
ADD sparco-ci.sh /usr/local/bin/sparco-ci

RUN set -eux ; \
    if [ "$DO_BSMEM" -eq "1" ] ; then \
    mv /usr/local/bin/bsmem-ci /usr/local/bin/bsmem-ci-c && \
    mv /usr/local/bin/bsmem-ci.sh /usr/local/bin/bsmem-ci ; \
    fi
RUN set -eux ; \
    if [ "$DO_MIRA" -eq "1" ] ; then \
    ln -s /usr/local/bin/ymira /usr/bin ; \
    fi


# Use valgrind with wisard:
ADD valgrind-wisard-ci.sh $WISARD_DIR/bin/

RUN set -eux ; \
    if [ "$USE_VALGRIND" -eq "1" ] ; then \
    apt-get update && apt-get install -y --no-install-recommends valgrind && \
    rm /usr/bin/wisard-ci && \
    ln -s $WISARD_DIR/bin/valgrind-wisard-ci.sh /usr/bin/wisard-ci && \
    echo "---> valgrind + wisard ready !" ; \
    fi


# Auto-deploy OImaging-uws.war on startup
# notes:
# - CONTEXT_PATH is not honored : app dir is the basename of the war (OImaging-uws here)
# - use webapps-javaee to take advantage of tomcat10 migration keeping our war using "old" Java EE before moving to Jakarta EE
ENV INSTALL_WEBAPPDIR=$CATALINA_HOME/webapps-javaee
RUN mkdir $INSTALL_WEBAPPDIR && \
    chown -R tomcat:tomcat $INSTALL_WEBAPPDIR && \
    echo "Installing war into the migration webapps-javaee folder : $INSTALL_WEBAPPDIR"
ADD OImaging-uws.war $INSTALL_WEBAPPDIR


# Add run_catalina.sh to set env vars:
ADD run_catalina.sh /usr/local/tomcat/bin/
ADD setenv.sh /usr/local/tomcat/bin/

CMD ["run_catalina.sh"]

# Define USER to restrict privileges (not root) once image is production ready:
USER tomcat
EXPOSE 8080

# Set max server jobs (default 4):
ARG SERVER_MAX_JOBS=4
ENV SERVER_MAX_JOBS=${SERVER_MAX_JOBS}
