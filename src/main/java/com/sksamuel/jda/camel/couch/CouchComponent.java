package com.sksamuel.jda.camel.couch;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:04:15
 * 
 */
public class CouchComponent extends DefaultComponent {

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
		Endpoint e = new CouchEndpoint();
		setProperties(e, params);
		return e;
	}

}
