package com.sksamuel.camel.couchdb;

import org.lightcouch.Changes;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbContext;
import org.lightcouch.Response;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 16:28:38
 * 
 */
public class CouchDbClientWrapper {

	private final CouchDbClient	client;

	public CouchDbClientWrapper(CouchDbClient client) {
		this.client = client;
	}

	public Response save(Object doc) {
		return client.save(doc);
	}

	public Changes changes() {
		return client.changes();
	}

	public CouchDbContext context() {
		return client.context();
	}

}
