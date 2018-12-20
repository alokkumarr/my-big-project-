#
# Docker image for building Advanced Analytics modules on continuous
# integration build agents.
#
# Note: A Docker container is used for the build process as it gives
# greated control of the build environment, allowing the build itself
# to install all required packages and dependencies inside the
# container.  The continuous integration environment then only needs
# to provide Docker support.
#

# Use CentOS base image
FROM centos:7.4.1708

# Install the EPEL repository to make the R package available for
# installation
RUN yum -q -y install epel-release

# Install the R language environment
RUN yum -q -y install R-3.5.1

# Install dependencies for SIP Advanced Analytics R packages
RUN yum -q -y install openssl-devel libcurl-devel libxml2-devel \
  libX11-devel libpng-devel mesa-libGLU-devel

# Execute script to preinstall R package dependencies, to speed up
# repeated builds with the help of Docker image build cache
COPY rpreinstall /root/build/rpreinstall
RUN /root/build/rpreinstall

# Add SIP A2 R packages to build
ADD a2modules.tar /root/build

# Run build
COPY rbuild /root/build/rbuild
