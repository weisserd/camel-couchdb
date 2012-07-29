package com.sksamuel.camel.couchdb;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:04:15
 * 
 */
public class CouchDbComponent extends DefaultComponent {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchDbComponent.class);

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
		Endpoint e = new CouchDbEndpoint(uri, remaining, this);
		setProperties(e, params);
		logger.info("Created CouchDB Endpoint [{}]", e);
		return e;
	}
}
