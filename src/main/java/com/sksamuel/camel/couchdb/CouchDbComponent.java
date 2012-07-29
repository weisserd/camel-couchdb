package com.sksamuel.camel.couchdb;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:04:15
 * 
 */
public class CouchDbComponent extends DefaultComponent {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchDbComponent.class);

	public CouchDbComponent() {
	}

	public CouchDbComponent(CamelContext context) {
		super(context);
	}

	@Override
	protected CouchDbEndpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
		CouchDbEndpoint e = new CouchDbEndpoint(uri, remaining, this);
		setProperties(e, params);
		logger.info("Created CouchDB Endpoint [{}]", e);
		return e;
	}
}
