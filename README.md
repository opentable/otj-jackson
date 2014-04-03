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

The [OpenTableJacksonModule](https://github.com/opentable/otj-jackson/blob/master/src/main/java/com/opentable/jackson/OpenTableJacksonModule.java)
binds configured Jackson `ObjectMapper`s in the Guice injector.  Normally this is installed by a
[server template](https://github.com/opentable/otj-server).

Component Level
---------------

*Intermediate*

Some dependencies on foundation components are allowed, but as always it should be minimal.

----
Copyright (C) 2014 OpenTable, Inc.
