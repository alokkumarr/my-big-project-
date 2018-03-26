# Setup instructions for using Docker Machine in the cloud

Execute the following steps to deploy SAW using Docker Machine in the
cloud:

1. Download the [Docker Machine] binary

2. Rename it to `docker-machine` (or `docker-machine.exe` on Windows)
   and put it on your `PATH`

3. Configure the following environment variables on your computer:

        SAW_AWS_REGION=us
        SAW_AWS_ACCESS_KEY_ID=AKIAIMXBE56B6CRN6GKA
        SAW_AWS_SECRET_ACCESS_KEY=<secret>
        SAW_AWS_USERNAME=<user0001>

4. Note: The `SAW_AWS_REGION` variable must be set to either `us` (for
   the U.S.)  or `in` (for India).  Ask a SAW team member for the
   secret access key value.  The `SAW_AWS_USERNAME` variable must be
   set to your Synchronoss Active Directory username.

5. Run the following commands to build and deploy SAW to the remote
   Docker Machine host:

        $ mvn package
        $ mvn -Ddocker-start=cloud

6. Get the SAW start page URL by running the following command:

        $ docker-machine ssh user-$SAW_AWS_USERNAME saw-url

7. Navigate to the SAW start page URL in your browser

Note: The first run will take longer to complete, up to 20 minutes.
Subsequent runs using the same remote machine will be faster and take
only a couple of minutes.  Also please note that remote machines are
automatically shut down after a couple of hours, to reduce costs.
Contents of the machine are preserved and it can be started up again
if needed.

[Docker Machine]: https://github.com/docker/machine/releases/

# Logging in to the remote Docker Machine host

To log in to the remote Docker Machine host, execute the following
command:

        $ docker-machine ssh user-$SAW_AWS_USERNAME

# Automatic shutdown of remote Docker Machine host

The remote Docker Machine host has a default timeout of a couple of
hours (configured in `dist/src/test/cloud/cloud-config.yaml`) after
which it shuts down to save on costs.  If that happens, the host will
automatically be started up again when executing a Docker Machine
command the next time.  This speeds up subsequent SAW deploys because
the remote host will already have been provisioned and has the Docker
build cache ready.

If you want to remove a stopped Docker Machine host completely,
execute the following command:

        $ docker-machine rm user-$SAW_AWS_USERNAME

The remote host will automatically be recreated the next time SAW is
deployed using Docker Machine.

# Troubleshooting

If you run into problems when trying to deploy SAW using Docker
Machine, try the solutions described in the sections below.

## Connection to the remote host times out

If the connection to the Docker Machine host is timing out, please
check in the AWS console that your [public IP] has been added to
`docker-machine` security group for all ports.  If it is not there,
add your public IP to it.  Then retry deploying.

[public IP]: http://ipecho.net/

## Key pair already exists

If there is an error message about the key pair already existing, go
to the AWS console and delete that key pair.  Then retry deploying.
If you don't have access to the AWS console yourself, ask a team
member for help.

## Invalid certificats

If a Docker Machine was previously stopped and later on is started
again, it might get a new IP address from AWS.  In this case you will
get an invalid certificate error.  To regenerate certificates, execute
the following command:

        $ docker-machine regenerate-certs user-$SAW_AWS_USERNAME

Then retry deploying again.

## Starting from scratch

If everything else fails or you get a difficult to troubleshoot error,
execute `docker-machine rm user-$SAW_AWS_USERNAME` to delete the
Docker Machine host and start again from scratch.

# Deploying from the continuous integration server

If you cannot deploy using Docker Machine from your own development
machine, see instructions for deploying from the [continuous
integration server].

[continuous integration server]: development-cloud-ci.md
