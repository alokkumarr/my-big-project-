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
        $ mvn -Pdocker-start=cloud

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
[public IP]: http://ipecho.net/

# Logging in to the remote Docker Machine host

To log in to the remote Docker Machine host, execute the following
command:

        $ docker-machine ssh user-$SAW_AWS_USERNAME

# Connection to the remote host is timing out

If the connection to the remote host is timing out, please check in
the AWS console that your [public IP] has been added to
`docker-machine` security group for all ports.  If it is not there,
add your public IP to it.
