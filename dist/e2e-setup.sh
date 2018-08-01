#!/usr/bin/env bash
#Please check with sip-team-a automation team before making any changes to this file
# ########### Uses of this file ######
# In this shell script we are replacing default protractor command with 'e2e-full-suite'.
# So we replace `<arguments>run protractor -- --baseUrl=` with `<arguments>run e2e-full-suite -- --baseUrl=` .
# And it modifies dist/pom.xml so that when mvn clean verify runs as build process then all e2e test gets exeuted
# Note: by default this shell script is not used and we are using this shell script in our test Bamboo plan instead of regular build plan.
sed -i 's/<arguments>run protractor -- --baseUrl=/<arguments>run e2e-full-suite -- --baseUrl=/g' pom.xml