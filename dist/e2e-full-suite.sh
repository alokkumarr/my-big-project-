#!/usr/bin/env bash
#Please check with Anudeep Patel(Anudeep.Singh@synchronoss.com) before making anychanges to this file
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