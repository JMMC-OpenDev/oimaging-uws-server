#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************

#
# WISARD wrapper for OIMAGING
#


# TRAP: Do not leave children jobs running if the shell has been cancelled
cleanup_trap() {
    CHILDREN_PIDS=$(jobs -p)
    if [ -n "$CHILDREN_PIDS" ]
    then
        trap - EXIT
        echo -e "SHELL cancelled, stopping $CHILDREN_PIDS"
        # we may try to send only TERM before a pause and a last loop with KILL signal ?
        kill $CHILDREN_PIDS

        echo -e "SHELL cancelled, waiting on $CHILDREN_PIDS"
        # wait for all pids
        for pid in $CHILDREN_PIDS; do
            wait $pid
        done

        CHILDREN_PIDS=$(jobs -p)
        if [ -n "$CHILDREN_PIDS" ]
        then
            echo -e "SHELL cancelled, killing $CHILDREN_PIDS"
            kill -9 $CHILDREN_PIDS
        fi
  fi
}
trap cleanup_trap EXIT


# HERE BEGINS THE SCRIPT

#make FULLSCRIPTNAME and SCRIPTROOT fully qualified
FULLSCRIPTNAME=$(readlink -f $0)
SCRIPTNAME=$(basename $FULLSCRIPTNAME)
SCRIPTROOT=$(readlink -f $( dirname $FULLSCRIPTNAME)/..)

#source main environment if any
if [ -e "$SCRIPTROOT/bin/env.sh" ]
then
  source $SCRIPTROOT/bin/env.sh
fi

# Print usage and exit program
function printUsage ()
{
  echo -e "Usage: $SCRIPTNAME [-h] [-v] [-d] [-f fov] [-i init_img] <input> <output>"
  echo -e "\t-h\tprint this help."
  echo -e "\t-v\tprint version. "
  echo -e ""
  echo -e "The following options are available in manual mode only, not through the Oimaging interface:"
  echo -e "\t-d : display graphs (needs interactive mode, defined as the existence of an attached Terminal to the process)."
  echo -e "\t-A : use All data = ignore flagged data."
  echo -e "\t-i init_img : pass startup (guess) image (FITS format)."
  echo -e "\t-f fov : field-of-view in mas."
  echo -e "\t-n nbiter : number of iterations."
  echo -e "\t-N np_min : number of reconstructed resels."
  echo -e "\t-w [wmin,wmax] : wavelength range, in microns. No blanks in the argument, e.g.: [1.5,3.3] ."
  echo -e "\t-r regularisation name : one of 'TOTVAR','PSD', 'L1L2', 'L1L2WHITE', 'SOFT_SUPPORT' "
  echo -e "\t-V level : verbosity level [0-3] "
  exit 1
}

# Print version and exit program
function printVersion ()
{
  # WISARD_CI_VERSION is declared as env var in the DockerFile
  if [ -z "$WISARD_CI_VERSION" ]
  then
    echo "WISARD_CI_VERSION undefined"
  else
    echo $WISARD_CI_VERSION
  fi
  exit 0
}


# Parse command-line parameters
while getopts "hvdAi:f:n:N:w:r:V:" option
do
    case $option in
        h )
            printUsage ;;
        v )
            printVersion ;;
        d ) # display
            DISPLAYCOMMAND=', /display ';
	    ;;
        A ) # all (flagged and not flagged) data
            FLAG_COMMAND=', /use_flagged ';
	    ;;
        i ) # init_image
            INIT_IMAGE_COMMAND=', init_img='\'"$OPTARG"\'' ';
	    ;;
        f ) # fov
            FOVCOMMAND=', fov='"$OPTARG"' ';
	    ;;
        n ) # nbiter
            NBITER_COMMAND=', nbiter='\'"$OPTARG"\'' ';
	    ;;
        N ) # np_min
            NPMIN_COMMAND=', np_min='\'"$OPTARG"\'' ';
	    ;;
        w ) # waverange
            WAVERANGE_COMMAND=', waverange='\'"$OPTARG"\'' ';
	    ;;
        r ) # regul
            REGUL_COMMAND=', regul='\'"$OPTARG"\'' ';
	    ;;
        V ) # VERBOSITY
            VERB_COMMAND=', verb='$OPTARG' ';
	    ;;
        * ) # Unknown option
            echo "Invalid option -- $option"
            printUsage ;;
    esac
done

let SHIFTOPTIND=$OPTIND-1
shift $SHIFTOPTIND

# command-line parameters will be given to WISARD
# just check
if [ $# -lt 2 ]
then
    echo "ERROR: Missing arguments: required input and output files"
    printUsage
fi

INPUT="$(readlink -f $1)"
OUTPUT="$(readlink -f $2)"

FITS_VERIFY=`cat /opt/FITS_VERIFY`
# Check INPUT:
if [ "$FITS_VERIFY" -eq "1" ] ; then
  echo ""
  echo "--- fitsverify $INPUT ---"
  fitsverify -q $INPUT
  if [ $? != 0 ] ; then fitsverify $INPUT; fi
  echo "---"
fi

# Run execution
cd $WISARD_DIR

echo "WISARD_CI_VERSION: ${WISARD_CI_VERSION}"

# If env var is defined, assume we are remote on the JMMC servers.
if [ -z "$WISARD_CI_VERSION" ]
then
  if [ -z "$IDL_STARTUP" ] #if we have no IDL env available....
  then
    export GDL_STARTUP="gdl_startup.pro"
    echo "DEBUG using startup procedure $GDL_STARTUP"
  else
    echo "DEBUG using startup procedure $IDL_STARTUP"
  fi
else
  # add helper to launch gdl properly. this procedure should insure that the IDL/GDL !PATH contains idlastro procedures (readfits.pro etc).
  export GDL_STARTUP="gdl_startup.pro"
fi

WISARD_CI_COMMAND="wisardgui,'"$INPUT"','"$OUTPUT"'"${DISPLAYCOMMAND}${FLAG_COMMAND}${INIT_IMAGE_COMMAND}${FOVCOMMAND}${NBITER_COMMAND}${NPMIN_COMMAND}${WAVERANGE_COMMAND}${REGUL_COMMAND}${VERB_COMMAND}

echo "cmd: gdl -e \"$WISARD_CI_COMMAND\""
gdl -e "$WISARD_CI_COMMAND" 2>&1

# Check OUTPUT:
if [ "$FITS_VERIFY" -eq "1" ] ; then
  echo ""
  echo "--- fitsverify $OUTPUT ---"
  fitsverify -q $OUTPUT
  if [ $? != 0 ] ; then fitsverify $OUTPUT; fi
  echo "---"
fi

