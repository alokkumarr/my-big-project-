#
# SIP testing base image with MapR client configured
#

# Start from SIP base image
FROM sip-base:${project.version}

ARG secure_enviroment
# Install MapR client
RUN yum -y -q install mapr-client

RUN echo $secure_enviroment
# Configure MapR client
RUN if [ $secure_enviroment == "True" ]; then \
    /opt/mapr/server/configure.sh -N sip -c -C sip-mapr:7222 -secure -dare ; \
    else \
    /opt/mapr/server/configure.sh -N sip -c -C sip-mapr:7222 ; \
    fi

# Workaround: Despite the "log4j.log" file path workaround in
# "dist/src/main/ansible/roles/saw-maprclient/tasks/main.yml", there
# seems to be additional logging configuration embedded in MapR
# libraries that tries to write to a file "log4.log" in the current
# working directory.  SAW services are run as the "mapr" user and do
# not have write permissions to "log4.log" in the root directory, and
# generate an error and long stacktrace in the logs.  This is repeated
# for every Transport Service and Executor process.  So create a
# writable file as a workaround to silence these errors.
RUN log=log4.log && touch $log && chmod a+w $log
