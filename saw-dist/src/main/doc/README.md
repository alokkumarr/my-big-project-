# Introduction

This is the Synchronoss Analytics Workbench (SAW) release package.  It
provides documentation and tools for all SAW modules and the SAW
modules themselves in a single package.

# Deployment

To deploy SAW to an environment, execute the following command in this
directory:

        ./saw-deploy <saw-config>

The lone argument is the path to a SAW environment configuration file,
which needs to be set up for each environment individually.  A sample
configuration can be found in the `saw-config` file, which defaults to
installing on "localhost".

For more details on deploying SAW, see the SAW Operations Guide, which
is published in the same place as the SAW release package.
