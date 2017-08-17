# Introduction

This is the SAW bundle package.  It provides documentation and tools
common to all SAW modules and the modules themselves in a single
package.

# Deployment

To deploy SAW to an environment, execute the following command in this
directory:

        ./deploy <config>

The lone argument is the path to a SAW environment configuration file,
which needs to be set up for each environment individually.  A sample
configuration can be found in the `config` file, which defaults to
installing on "localhost".  For more details on deploying SAW, see the
Operations Guide in the `doc` directory.
