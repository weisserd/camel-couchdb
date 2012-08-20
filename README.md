camel-couchdb
==========

The camel-couchdb component allows you to treat Apache CouchDB as a producer or consumer of messages. Using the lightweight LightCouch API, this camel component has the following features:

* As a consumer, monitors couch changesets for inserts, updates and deletes and publishes these as messages into camel routes.
* As a producer, can save or update documents into couch.
* Can support as many endpoints as required, eg for multiple databases across multiple instances.
* Ability to have events trigger for only deletes, only inserts/updates or all (default).
* Headers set for sequenceId, document revision, document id, and HTTP method type.

How to Use
==========

You can use couch as an endpoint with the following URI. The endpoint URI is the same for producer and consumer.
`couchdb:http://hostname[:port]/database?[options...]`

Some of the available options are:

* _deletes_ document deletes are published as events (default true)
* _updates_ document inserts/updates are published as events (default true)
* _heartbeat_ how often to send an empty message to keep socket alive (default 30000ms)
* _createDatabase_ create the database if it does not already exist (default false)
* _username_ username in case of authenticated databases (default null)
* _password_ password for authenticated databases (default null)

Meta Data
=========

Messages published into the system have the following metadata applied in the form of headers on the exchange:

* CouchDbDatabase - the database the message came from
* CouchDbSeq - the couchdb changeset sequence number of the update / delete message
* CouchDbId - the couchdb document id
* CouchDbRev - the couchdb document revision
* CouchDbMethod - the method (delete / update)

Tests
=====

camel-couchdb has a complete set of unit tests. In addition there are some integration tests that require you to run a local instance of Couch. By default the tests will look at localhost:5984 and for a database called camelcouchdb.

- Initial contribution by Stephen Samuel.