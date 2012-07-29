package com.sksamuel.camel.couchdb;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.Gson;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:44
 * 
 */
public class CouchDbProducer extends DefaultProducer {

	private final CouchDbEndpoint	endpoint;
	private final CouchDbClient	couchClient;

	public CouchDbProducer(CouchDbEndpoint endpoint, CouchDbClient couchClient) {
		super(endpoint);
		this.couchClient = couchClient;
		this.endpoint = endpoint;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			Object doc = exchange.getIn().getMandatoryBody();
			if (doc instanceof String)
				doc = new Gson().toJsonTree(doc);
			Response save = couchClient.save(doc);
			if (save == null)
				throw new CouchDbException("Could not save document");
			exchange.getIn().setHeader(CouchDbEndpoint.REV, save.getRev());
			exchange.getIn().setHeader(CouchDbEndpoint.ID, save.getId());
		} catch (Exception e) {
			throw new CouchDbException(e);
		}
	}
}
