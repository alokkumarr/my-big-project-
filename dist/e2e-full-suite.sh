#!/usr/bin/env bash
#Please check with sip-team-a automation team before making any changes to this file
# ########### Uses of this file ######
# This shell script is used by maven build cycle and it gets invoked by dist/pom.xml
# When below code(dist/pom.xml) gets executed then it runs npm command i.e. 'npm run e2e-full-suite -- --baseUrl=some_url'
#          <execution>
#            <id>npm-run-protractor</id>
#            <phase>integration-test</phase>
#            <goals>
#              <goal>npm</goal>
#            </goals>
#            <configuration>
#              <arguments>run e2e-full-suite -- --baseUrl=http://${docker.host.address}:${saw.docker.port}/saw/web/</arguments>
#            </configuration>
#          </execution>
# and then 'e2e-full-suite' script get executed from dist/package.json which interanlly triggers this shell script.  
#
echo "current working dir"
pwd
echo "moving to saw-web working dir"
cd ../saw-web
url=$1
echo "base url for testing:$url"
echo "current working dir"
pwd
echo "Delete allure reports if any.."
rm -rf target/allure-results
echo "running npm install"
npm install
echo "starting webdriver manager update"
node node_modules/protractor/bin/webdriver-manager update
echo "starting protractor test"
echo "running protractor tests node_modules/protractor/bin/protractor conf/protractor.conf.js "$url""
node_modules/protractor/bin/protractor conf/protractor.conf.js "$url"
#node_modules/protractor/bin/protractor conf/protractor.conf.js --baseUrl="http://54.159.35.28/saw/web/"