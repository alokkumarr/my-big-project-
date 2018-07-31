#!/usr/bin/env bash
#Please check with Anudeep Patel(Anudeep.Singh@synchronoss.com) before making anychanges to this file
sed -i 's/<arguments>run protractor -- --baseUrl=/<arguments>run e2e-full-suite -- --baseUrl=/g' pom.xml