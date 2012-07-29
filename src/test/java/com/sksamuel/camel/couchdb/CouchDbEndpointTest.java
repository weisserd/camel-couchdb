package com.sksamuel.camel.couchdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.junit.Test;

import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 16:45:20
 * 
 */
public class CouchDbEndpointTest {

	@Test
	public void testCreateCouchExchangeHeadersAreSet() throws URISyntaxException {
		CouchDbEndpoint endpoint = new CouchDbEndpoint("couchdb:http://localhost/db", "http://localhost/db", new CouchDbComponent());

		String id = UUID.randomUUID().toString();
		String rev = UUID.randomUUID().toString();
		String seq = "seq123";

		JsonObject doc = new JsonObject();
		doc.addProperty("_id", id);
		doc.addProperty("_rev", rev);

		Exchange exchange = endpoint.createCouchExchange(seq, id, doc, false);
		assertEquals(id, exchange.getIn().getHeader(CouchDbEndpoint.HEADER_DOC_ID));
		assertEquals(rev, exchange.getIn().getHeader(CouchDbEndpoint.HEADER_DOC_REV));
		assertEquals(seq, exchange.getIn().getHeader(CouchDbEndpoint.HEADER_SEQ));
		assertEquals("UPDATE", exchange.getIn().getHeader(CouchDbEndpoint.HEADER_METHOD));
		assertEquals("db", exchange.getIn().getHeader(CouchDbEndpoint.HEADER_DATABASE));

	}

	@Test
	public void assertSingleton() throws URISyntaxException {
		CouchDbEndpoint endpoint = new CouchDbEndpoint("couchdb:http://localhost/db", "http://localhost/db", new CouchDbComponent());
		assertTrue(endpoint.isSingleton());
	}

	@Test(expected = CouchDbException.class)
	public void testDbRequired() throws URISyntaxException {
		new CouchDbEndpoint("couchdb:http://localhost:80", "http://localhost:80", new CouchDbComponent());
	}

	@Test
	public void testDefaultPortIsSet() throws URISyntaxException {
		CouchDbEndpoint endpoint = new CouchDbEndpoint("couchdb:http://localhost/db", "http://localhost/db", new CouchDbComponent());
		assertEquals(CouchDbEndpoint.DEFAULT_PORT, endpoint.getPort());
	}

	@Test(expected = CouchDbException.class)
	public void testHostnameRequired() throws URISyntaxException {
		new CouchDbEndpoint("couchdb:http://:80/db", "http://:80/db", new CouchDbComponent());
	}

	@Test(expected = CouchDbException.class)
	public void testSchemeRequired() throws URISyntaxException {
		new CouchDbEndpoint("couchdb:localhost:80/db", "localhost:80/db", new CouchDbComponent());
	}
}
