# Synchronoss - XDF Next Gen 

The Synchronoss XDF NextGen version provides REST API to deal with Data Lake and Execute Components.

Deploy JAR files
===

To deploy jar files for core, meta-api modules,
Bamboo build plan

    [BDA Meta Data Deploy to Nexus|https://bamboo.synchronoss.net:8443/browse/BDA-BMD]

must be executed manually (**Run plan** button/link).

Bamboo plan is using for build GIT branch

**release/deploy**

Steps to release:

1. Complete development on development (master) branch.

2. Ensure correct release version is set in all pom files, command can be used:

`
$ mvn versions:set -DnewVersion=1.0.2
`

3. Merge development branch with correct version to **relese/deploy** branch.

4. Execute Bamboo build on Bamboo page

[BDA Meta Data Deploy to Nexus](https://bamboo.synchronoss.net:8443/browse/BDA-BMD)

This will copy JAR files to Nexus.


**NB:** Same version can not be deployed on Nexus twice!
