# look at the Makefile and Dockerfile for main building operations
#
# oiservices can run the service (UWS server) without args
#   or run a binary inside the container
#
# History 1.4 :
#   - update to bsmem 2.0.6
#   - update to wisard-ci V3_0_1Beta8
# History 1.5 :
#   - update to wisard-ci V3_0_2Beta1
#   - OImaging-uws : move from limit of // jobs from 3 to 16
# History 1.6 :
#   - add MiRA  (https://github.com/emmt/MiRA)
# History 1.7 :
#   - new wisard-ci version
# History 1.8 :
#   - OImaging UWS service now supports cliOptions
#   - update to bsmem 2.1.1
# History 1.9 :
#   - Use debian stretch + openjdk8
#   - Added MiRA
# History 2.0 :
#   - Use tomcat 8.5
#   - Use specific tomcat user
# History 2.1 :
#   - add cliOptions for mira-ci.sh
# History 2.2 :
#   - gcc tuning (march) for bsmem / gdl / mira
# History 2.3 :
#   - gcc tuning (march=ivybridge) + cleanup (-dev + .git) + enable OMP_THREAD_LIMIT=2
# History 2.4 :
#   - disable OpenMP (lower overhead) when building BSMEM, MiRA & Wisard
# History 2.5 :
#   - upgrade WISARD-CI to 3.1.7 + use march=broadwell
# History 2.6 :
#   - upgrade WISARD-CI to 3.1.8 (fix waverange filter)
#   - set docker memory limit to 8G
# History 2.7 :
#   - logs moved into tomcat/logs + job duration
# History 2.8 :
#   - upgrade WISARD-CI to 3.1.10 (fix NaN handling)
#   - upgrade GDL (fix crash in where)
# History 2.9 :
#   - upgrade WISARD-CI to 3.1.11 (fix wavelength processing)
#   - upgrade GDL (trunk)
# History 3.0 :
#   - upgraded MIRA
# History 3.1 :
#   - upgraded WISARD-CI to 3.1.12 (fix NaN / flags)
# History 3.2 :
#   - fixed log configuration + server-side timeout (2H)
# History 3.2.1 :
#   - fixed stats log (no repeat)
# History 3.3 :
#   - upgraded Docker image (debian buster + full up-to-date build @ 2019.10.22)
#   - added Sparco plugin (mira from FerreolS)
# History 3.3.3 :
#   - updated remote links and repository (bsmem, mira)
# History 3.4 :
#   - fixed build (dec 2020)
# History 3.5 :
#   - updated libs in Dockerfile
#   - bump WISARD-CI to 3.1.14
#   - enabled OPENMP back again
# History 3.6 :
#   - updated BSMEM to 2.3.0
#   - updated MiRA
# History 3.6.1 :
#   - upgraded WISARD-CI to 3.2.3 (params + mira initial image)
# History 3.6.2 :
#   - upgraded WISARD-CI to 3.2.4 (fix init image header with hdu name)
#   - upgraded BSMEM-CI (collect sources from gitlab.com)
# History 3.7.0 (2022.07.20):
#   - upgraded Sparco plugin (mira-sparco-multi from kluskaj)
# History 3.7.1 (2023.05.10):
#   - fix GDL build
# History 3.7.2 (2024.09.16):
#   - updated docker base image moving to tomcat10 using webapps-javaee migration folder


NAME=oiservices
VERSION=3.7.3

# EoF

