Changelog
=========

2.1.3
-----

* Disable Afterburner and Mr Bean by default. This will cost in deserialization performance, but prevent some
Java 9+ reflection issues. (Revisit post Jackson 3). These are configurable with the boolean switches
`ot.jackson.afterburner`, `ot.jackson.mrbean` - but be aware ASM might be updated (and is a dependency). 
When shipped, ASM was version 5.2 - which isn't J9+ compatible.
