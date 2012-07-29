package com.sksamuel.camel.couchdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 17:06:16
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CouchDbComponentTest {

	@Mock
	private CamelContext	context;

	@Test
	public void testEndpointCreated() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();

		String uri = "couchdb:http://localhost:5984/db";
		String remaining = "http://localhost:5984/db";

		Endpoint endpoint = new CouchDbComponent(context).createEndpoint(uri, remaining, params);
		assertNotNull(endpoint);
	}

	@Test
	public void testPropertiesSet() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createDatabase", true);
		params.put("username", "coldplay");
		params.put("password", "chrism");
		params.put("heartbeat", 1000);

		String uri = "couchdb:http://localhost:14/db";
		String remaining = "http://localhost:14/db";

		CouchDbEndpoint endpoint = new CouchDbComponent(context).createEndpoint(uri, remaining, params);
		assertEquals("http", endpoint.getProtocol());
		assertEquals("localhost", endpoint.getHostname());
		assertEquals("db", endpoint.getDatabase());
		assertEquals("coldplay", endpoint.getUsername());
		assertEquals("chrism", endpoint.getPassword());
		assertTrue(endpoint.isCreateDatabase());
		assertEquals(14, endpoint.getPort());
		assertEquals(1000, endpoint.getHeartbeat());
	}
}
