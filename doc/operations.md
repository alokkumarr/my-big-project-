# Introduction

This document describes how to install and configure SAW and its
modules in an environment.

# Prerequisites

Before starting an installation of SAW ensure the following has been
provided in the environment:

1. Install and configure a MapR version 5.2 cluster in the environment

2. Install and configure Apache Spark version 2.1 from the MapR
   Ecosystem Pack on the MapR cluster

3. Provision a MapR user in the MapR cluster (with user ID 500)

3. Provision a mail relay host

4. Provision a host to deploy from (the deploy host), running CentOS 7
   as the operating system.  This host will be used to run the deploy
   command and store the environment configuration.  The deploy host
   is typically common to the entire environment.

5. Provision a host for running SAW (the target host), with 32 GB of
   memory and CentOS 7 as the operating system.  The host should be
   dedicated to SAW and not run other applications.

6. Install and configure MapR client on the SAW target host

Please note that the target hosts must be allocated exclusively for
SAW use.  The underlying infrastructure up to the operating system
level are expected to be managed externally, while SAW fully manages
packages and configuration on the target hosts.

Currently SAW only supports deploying to a single target host.

# Configuring

Each SAW environment requires its own configuration.  A sample
configuration has been provided in the `config` file, which defaults
to installing on `localhost`.  At a minimum, copy this sample file and
change the `localhost` entries to the SAW target host.

The sample configuration defaults to installing all available modules:
SAW Web, SAW Services and SAW Security.  To install only a subset of
these modules, edit the environment configuration to only mention the
desired modules.

Note that the environment configuration file must be preserved between
installations and upgrades.  It is recommended to put it under version
control that is stored on the deploy host and backed up to another
location.

# Installing

Execute the following steps to install SAW:

1. Get the SAW release package (named `saw-*.tgz`)

2. Prepare a SAW environment configuration file, as described in the
   previous section

3. Extract the package and execute the deploy command, giving it the
   path to the environment configuration file as an argument

        tar -xzf saw-*.tgz
        cd saw
        ./deploy <config>

Note: Configure passwordless SSH access to the SAW target host for a
smoother installation experience.  The deploy command should be run as
a normal user.  The deploy command will use sudo to request privileges
for relevant operations.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.

# Listing services and checking their status

To list services and check the status of all SAW systemd units,
execute the following commands:

        $ sudo systemctl list-units saw-\*

Please note that it is normal for the `saw-scheduler` service to be in
an inactive state most of the time.  It is activated on a set schedule
by `saw-scheduler.timer`.

# Starting, stopping and restarting services

Under normal circumstances there should be no need to start, stop or
restart SAW services manually.  However, if needed it can be done
using the following commands:

        $ sudo systemctl start <saw-service>
        $ sudo systemctl stop <saw-service>
        $ sudo systemctl restart <saw-service>

Where `<saw-service>` is one of the SAW systemd services (for example
`saw-gateway`), which can be listed using the `sudo systemctl
list-units saw-\*` command shown in the previous section.

# Logs

The SAW systemd services system logs can be accessed using the `sudo
journalctl` command.  To view the logs of individual services, use the
`-u` option:

        $ sudo journalctl -u saw-\*

# Entrypoints

The SAW Web module and supporting services are exposed on port 80 of
the SAW target host, i.e. `http://<saw-target-host>/`.  The SAW Web
application will automatically discover the endpoints for SAW Security
and SAW Services based on the URL it is being served from.

Nothing else in the SAW deployment, except for port 80 on the SAW
target host, is accessed by external parties.

# Loading semantic metadata

To enable creating analyses in SAW, load semantic metadata as follows:

        $ ssh <saw-services-host>
        $ sudo -u mapr /opt/saw/service/bin/mdcli.sh -i \
            file://<nodes-json> -o file:///tmp/log.json

The semantic metadata JSON is stored in the `<nodes-json>` file.

# Large header settings

Include the below properties in NGINX server config file to support, http request with large header(more than 8K).

       client_body_buffer_size 32k;
       client_header_buffer_size 16k;
       large_client_header_buffers 8 64k;

# Clearing the Transport Service Executor queues

If the SAW report execution queue has filled up, for example due to
many long-running queries being executed, the queues can be cleared
using the following commands:

        $ ssh <mapr-host>
        $ stream=/main/saw-transport-executor-regular-stream
        $ sudo -u mapr maprcli stream topic delete -path $stream -topic executions
        $ stream=/main/saw-transport-executor-fast-stream
        $ sudo -u mapr maprcli stream topic delete -path $stream -topic executions

Please note that clearing the queues affects all users of the system
and report execution types.

# SAW Security: Creating a data security key (DSK)

SAW supports row level filtering using a data security key.

## Prerequisites

DSK configured columns should be present in ALL of the data
objects/artifacts referenced in the metrics.

## Step 1: 

Create the Security Group in SEC_GROUP table.
    
    ########################## Create SEC_GROUP samaple script ##############################
    INSERT INTO `SEC_GROUP` (`SEC_GROUP_SYS_ID`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`)     VALUES ('1', '1', '2017-10-04', 'system');
    
## Step 2: 

Create DSK attribute (fields/columns name) for corresponding security
group (SEC_GROUP created in step 1).
    
    ########################## DSK Attribute Sample script #############################
    INSERT INTO `sec_group_dsk_attribute` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `SEC_GROUP_SYS_ID`, `ATTRIBUTE_NAME`) VALUES ('1', '1', 'SESSION_ID');
    INSERT INTO `sec_group_dsk_attribute` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `SEC_GROUP_SYS_ID`, `ATTRIBUTE_NAME`) VALUES ('2', '1', 'CONTENT_CLASS');
    
## Step 3: 

Create DSK values for corresponding DSK attribute (DSK Attribute
created in step 2).

    ########################## DSK Value sample script ###############################
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('1', '1', 'AFF2948C-DCFF-4944-8553-51435518AF67');
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('2', '1', '945ca612-d3ad-4e6e-9c92-7cff86730235');
    INSERT INTO `sec_group_dsk_value` (`SEC_GROUP_DSK_VALUE_SYS_ID`, `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`, `DSK_VALUE`) VALUES ('3', '2', 'VIDEOS'); 
    
## Step 4: 

Map the SEC_GROUP to users to apply the DSK filter.
    
    ########################## Update User with DSK script ##############################
    UPDATE USERS SET SEC_GROUP_SYS_ID = '3' WHERE USER_ID = 'analyst.dsk.mct.report';

Important Note: If any metrics contains more than one data object as
analysis for report then DSK attribute should be configured with
dataObjectName.columnName. Example: For MCT_SESSION data object, DSK
Attribute name should be MCT_SESSION.SESSION_ID.
