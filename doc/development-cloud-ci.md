# Deploying SAW from the continuous integration server

If you cannot run Docker [locally] or deploy from your development
machine using Docker Machine to the [cloud], there is a third option
of deploying a SAW development environment from the continuos
integration server (Bamboo).  Follow these steps:

1. Navigate to the [Bamboo plan] for deploying SAW to the cloud
2. Use the dropdown menu below the plan name to select the branch to
   deploy
3. Click the "Run" button and select "Run branch"
4. Wait for the build to complete successfully
5. Navigate to the "Artifacts" tab
6. Click the "SAW start page" artifact
7. Follow the link to the SAW environment start page

Note: The deployed SAW environment will be automatically shut down
after a couple of hours to save on costs.

[locally]: development.md
[cloud]: development-cloud.md
[Bamboo plan]: https://bamboo.synchronoss.net:8443/browse/BDA-SAWAD
