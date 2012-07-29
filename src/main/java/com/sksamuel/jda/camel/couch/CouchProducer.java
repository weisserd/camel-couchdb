package com.sksamuel.jda.camel.couch;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.lightcouch.CouchDbClient;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:44
 * 
 */
public class CouchProducer extends DefaultProducer {

	private final CouchEndpoint	endpoint;
	private final CouchDbClient	couchClient;

	public CouchProducer(CouchDbClient couchClient, CouchEndpoint endpoint) {
		super(endpoint);
		this.couchClient = couchClient;
		this.endpoint = endpoint;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
	}

}
