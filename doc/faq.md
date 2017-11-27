# What should I do if I run into a difficult to understand build issue?

Before spending more time on analyzing it, try the following to ensure
you have a working baseline to start from:

1. Revert the working directory to the latest unmodified version of
   the `master` branch

2. Run `git clean -fdx` to remove any possible leftover files, build
   artifacts, caches and similar that might be causing an issue
   
3. Retry the original build command that was failing

4. If the build is no longer failing, reapply local modifications and
   retry the build command.  If it works now, you probably had some
   leftover files in the working directory causing the issue.  If it
   still fails, the local modifications might have caused it.

Note: The `git clean -fdx` command will delete all files in the
working directory that are not under version control.  Take backups of
any local modifications first if you are not familiar with how that
command works.

# How can I run a command inside the SAW Docker container?

Execute the following command to get a shell inside the SAW Docker
container:

        docker exec -it saw bash

# What can I do if my Docker container reports running out of disk space?

Try running the following command to free up disk space:

        docker system prune --volumes

But be aware of that it will remove all images and volumes that are
not in use by any container.

# What should I do if building SAW Transport Service fails with a permission denied error?

If building SAW Transport Service gives an error `Failed to create
assembly: Error creating assembly archive package: Problem copying
files :
/Users/Shared/WORK/SAW-BE/saw-transport-service/target/saw-transport-service-2-package/saw-transport-service-2/lib/aopalliance.aopalliance-1.0.jar
(Permission denied)`, then remove the `saw-transport-service/target`
directory and retry.

# How can I update the SAW Transport Service routes?

After editing the `saw-services/saw-transport-service/conf/routes`
file, the Play framework generated source code files need to be
regenerated.  This is done as follows:

        $ saw-services/saw-transport-service/generate-routes

Note: This is a workaround until the SAW Transport Service has been
migrated to Java and Spring Framework.

The generated files pattern can be found in the
`saw-services/saw-transport-service/generate-routes` script near the
`rm -rf` command.
