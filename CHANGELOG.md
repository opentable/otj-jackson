Changelog
=========
5.2.0
-----
* Recompile for Spring 5.2
* Add ability inject custom modules

2.1.6
-----
* ObjectMapper default forced to turn colons off. Jackson
2.11 changes this, breaking compatibility. 

2.1.5
-----
* ot.jackson.relaxed-parser is false by default

2.1.4
-----
* As part of the AU stack convergence we are turning two JACKSON features on by default

Feature.ALLOW_SINGLE_QUOTES - allows single quotes in JSON
Feature.ALLOW_UNQUOTED_FIELD_NAMES - allows unquoted field names in json

Because this is MORE lenient, it probably won't cause issues. If it does, or you prefer a more strict JSON
import, set ot.jackson.relaxed-parser=false


2.1.3
-----

* Disable Afterburner and Mr Bean by default. This will cost in deserialization performance, but prevent some
Java 9+ reflection issues. (Revisit post Jackson 3). These are configurable with the boolean switches
`ot.jackson.afterburner`, `ot.jackson.mrbean` - but be aware ASM might be updated (and is a dependency). 
When shipped, ASM was version 5.2 - which isn't J9+ compatible.
