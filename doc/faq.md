# How can I run a command inside the SAW Docker container?

Execute the following command to get a shell inside the SAW Docker
container:

        docker exec -it saw bash

# What should I do if building SAW Transport Service fails with a permission denied error?

If building SAW Transport Service gives an error `Failed to create
assembly: Error creating assembly archive package: Problem copying
files :
/Users/Shared/WORK/SAW-BE/saw-transport-service/target/saw-transport-service-2-package/saw-transport-service-2/lib/aopalliance.aopalliance-1.0.jar
(Permission denied)`, then remove the `saw-transport-service/target`
directory and retry.

# What can I do if my Docker container reports running out of disk space?

Try running the following command to free up disk space:

        docker system prune

To free up even more disk space try the following command:

        docker system prune -a --volumes

But be aware of that it will remove all images and volumes that are
not in use by any container.
