[![Build Status](https://travis-ci.org/opentable/otj-jackson.svg)](https://travis-ci.org/opentable/otj-jackson)
OpenTable Jackson Component
===========================

Component Charter
-----------------

* OpenTable specific Jackson code.

Jackson
-------

The [Jackson JSON Processor](https://github.com/fasterxml/jackson) is one of the fastest and most flexible
JSON processing libraries available for Java.

It is important that all code configures Jackson similarly - the set of installed modules, various serialization settings,
and deserialization configuartion must be reasonably harmonious for compatibility.

The [OpenTableJacksonConfiguration](https://github.com/opentable/otj-jackson/blob/master/src/main/java/com/opentable/jackson/OpenTableJacksonConfiguration.java)
binds a configured Jackson `ObjectMapper`s in the Spring context.  Normally this is imported by the
[server](https://github.com/opentable/otj-server).

We also provide a high performance UUID serializer and deserializer.

Component Level
---------------

*Intermediate*

Some dependencies on foundation components are allowed, but as always it should be minimal.

----
Copyright (C) 2014 OpenTable, Inc.
