#!/usr/bin/python
##
# This file contains vars and funcs for
# use in BDA applications
# Version: WIP
# Author: S.Volkov
#         others are welcome
#

import logging, logging.handlers
import time
import os
import sys
import re

# This script name from command line
cmd_arg = sys.argv[0]
# This script directory
cmd_dir = os.path.dirname(os.path.abspath(cmd_arg))
# Script base name
cmd_str = os.path.basename(cmd_arg)
# Command name - script base name strip file extention
cmd_nam = cmd_str.rsplit('.',1)[0]

appl_info_file = ''
appl_info_dict = {}
def appl_info(var=None,dflt=None):
    if var:
        if dflt:
            return appl_info_dict.get(var, dflt)
        else:
            return appl_info_dict[var]
    else: # return the whole dictionary
        return appl_info_dict
##
# Special file names
# below: read values from bin/appl_info
vars_fnm = "/etc/bda/saw-security.vars"
log_dir  = "/var/bda/saw-security/log"

# Logger levels
# CRITICAL:50, ERROR:40, WARNING:30, INFO:20, DEBUG:10, NOTSET:0
#LOG_LEVEL=logging.WARNING # production
LOG_LEVEL=logging.DEBUG   # development

# Creates new logger with log on terminal
def init_logger( tag = cmd_nam ):
    logger = logging.getLogger(tag)
    logger.setLevel(LOG_LEVEL)
    # Use UTC in log records
    logger.converter = time.gmtime
    # create formatter for console handler
    formatter = logging.Formatter('%(levelname)s - %(message)s')
    # create console handler
    ch = logging.StreamHandler() # sys.stderr
    ch.setFormatter(formatter)
    # Create syslog handler
    syslog = logging.handlers.SysLogHandler(address = '/dev/log')
    # add the handlers to the logger
    logger.addHandler(ch)
    logger.addHandler(syslog)
    return logger

# Add file handler to logger
def init_logger_file( logger, log_fnm ):
    # Rotate log file is it exists
    if os.path.isfile(log_fnm):
        fh = logging.handlers.RotatingFileHandler(log_fnm, backupCount=9)
        fh.doRollover()
        fh.close()
    # create formatter for file handler
    formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
    # create file handler which logs even debug messages
    fh = logging.FileHandler(log_fnm)
    fh.setFormatter(formatter)
    logger.addHandler(fh)
    return logger

# NB: console only logger, must be initialized with init_logger_file(..) call
logger = init_logger(cmd_nam)
def get_logger() : return logger
#logger = init_logger_file(logger_var,log_fnm)

def read_appl_info( appl_info_file ):
    logger.debug("in read_appl_info")
    if not os.path.isfile(appl_info_file):
        raise Exception("appl_info file not found: '%s'" % appl_info_file)
    appl_info_dict = {}
    logger.info("open appl_info file %s", appl_info_file)
    with open(appl_info_file) as f:
        #while s = f.readline()
        for s in f:
            #logger.debug("s: %s", s)
            mm = re.match(r'^\s*(\S+?):\s*(\S+)', s)
            if not mm:
                continue
            var = mm.group(1)
            val = mm.group(2)
            logger.debug("%s = %s", var, val)
            appl_info_dict[var] = val
    f.close()
    logger.debug("appl info count: %d", len(appl_info_dict))
    logger.debug("out read_appl_info")
    return appl_info_dict

# Read variables and their values from the file
def read_vars_file( vars_fnm ):
    logger.debug("in read_vars_file")
    if not os.path.isfile(vars_fnm):
        raise Exception("Vars file not found: '%s'" % vars_fnm)
    vars_dict = {}
    logger.info("open vars file %s", vars_fnm)
    with open(vars_fnm) as f:
        #while s = f.readline()
        for s in f:
            s = re.sub(r"\s*#.*$","",s)
            if not ("=" in s):
                continue
            s = s.strip()
            s = re.sub(r"\s*=\s*","=",s)
            vv = s.split("=",1)
            logger.debug("%s = %s", vv[0], vv[1])
            vars_dict[vv[0]] = vv[1]
    f.close()
    logger.debug("vars count: %d", len(vars_dict))
    logger.debug("out read_vars_file")
    return vars_dict

########################

# hook to provide for custom appl_info file
if __name__ == '__main__': # test
    if len(sys.argv)>1 and sys.argv[1]:
        appl_info_file = os.path.abspath(sys.argv[1])
        print "appl_info file:", appl_info_file

if not appl_info_file:
    # - search for appl_info file in dirs:
    #   cmd_dir,
    #   cmd_dir+'../bin',
    #   cmd_dir+'../../bin',
    #   cmd_dir+'../../../bin'
    for ds in ['', '../bin', '../../bin', '../../../bin']:
        fn = os.path.join(cmd_dir, ds, 'appl_info')
        if os.path.isfile( fn ):
            appl_info_file = os.path.abspath(fn)
            break
    if not appl_info_file:
        sys.stderr.write("appl_info file not found")
        sys.exit(1)

appl_info_dict = read_appl_info(appl_info_file)
logger.debug("appl_info: %d values", len(appl_info()))

if __name__ == '__main__': # test
    print "Log file:", log_fnm

    logger.info( "%s: %d entries"%(appl_info_file, len(appl_info())) )
    for var, val in appl_info().iteritems():
        logger.info( "%s = %s"%(var, val) )

    # check custom vars file
    if len(sys.argv)>2:
        vars_fnm = os.path.abspath(sys.argv[2])

    logger.info( "Run: %s in %s", cmd_str, cmd_dir )
    #logger.error("BOO error")
    #logger.warning("BOO warning")
    #logger.debug("BOO debug")

    logger.debug( "vars fnm: %s"%(vars_fnm) )

    vars_vals = read_vars_file( vars_fnm )
    if logger.isEnabledFor(logging.DEBUG):
        logger.info( "%s: %d entries"%(vars_fnm, len(vars_vals)) )
        for var, val in vars_vals.iteritems():
            logger.info( "%s = %s"%(var, val) )

    sys.exit(0)

# test run:
#$ cd /BDA/saw-security
#$ Database/Source/bda_init.py '' conf/templates/saw-security.vars.changeme
#$ Database/Source/bda_init.py bin/appl_info conf/templates/saw-security.vars.changeme
