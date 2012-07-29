camel-couchdb
==========

The camel-couchdb component allows you to treat Apache CouchDB as a producer or consumer of messages. Using the lightweight LightCouch API, this camel component has the following features:

* As a consumer, monitors couch changesets for inserts, updates and deletes and publishes these as messages into camel routes.
* As a producer, can save or update documents into couch.
* Can support as many endpoints as required, eg for multiple databases across multiple instances.
* Ability to have events trigger for only deletes, only inserts/updaes or all (default).
* Headers set for sequenceId, document revision, document id, and HTTP method type.

How to Use
==========

You can use couch as an endpoint with the following syntax. The endpoint syntax is the same for producer and consumer.
`couchdb:http://hostname[:port]/database?[options...]`

Tests
=====

camel-couchdb has a complete set of unit tests. In addition there are some integration tests that require you to run a local instance of Couch. By default the tests will look at localhost:5984 and for a database called camelcouchdb.

- Initial contribution by Stephen Samuel.