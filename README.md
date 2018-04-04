# Introduction

This is the SAW bundle source code repository.  It provides
documentation and tools common to all SAW modules, which include SAW
Security, SAW Servies and SAW Web.

# Development

Start by reading the [Development Guide](doc/development.md) which
describes how to build the project and making releases.  The list
of [Frequently Asked Questions](doc/faq.md) describes commonly
occuring problems and solutions to them.  Documentation in
the [doc](doc) directory is internal and intended for developers.

Follow the [SAW project standards] when developing new features and
making changes.  Additionally see the shared [project standards].

[project standards]: https://confluence.synchronoss.net:8443/display/BDA/Project+Standards
[SAW project standards]: https://confluence.synchronoss.net:8443/display/BDA/SAW+Project+Standards

# Documentation

The [SAW Operations Guide] describes administration tasks such as
installing, configuring and monitoring.  The [SAW Design Guide]
describes the application and its design on a higher level.  Use it to
get an overview of the components and the dataflow.  Documentation in
the [dist/src/main/asciidoc](dist/src/main/asciidoc) directory
is intended for external users and is rendered and an delivered as
artifacts along the release package for external users.  The rendered
versions of this documentation can be accessed through the continuous
integration server's latest [build artifacts].

[SAW Operations Guide]: dist/src/main/asciidoc/saw-operations/index.adoc
[SAW Design Guide]: dist/src/main/asciidoc/saw-design/index.adoc
[build artifacts]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW/latest/artifact/shared/Documentation/index.html
