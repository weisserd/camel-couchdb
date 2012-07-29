package com.sksamuel.camel.couchdb;

import java.util.UUID;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 18:09:05
 * 
 */
public class CouchDbConsumerIntegrationTest extends CamelTestSupport {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchDbConsumerIntegrationTest.class);

	@EndpointInject(uri = "couchdb:http://localhost:5984/camelcouchdb")
	private Endpoint			from;

	@EndpointInject(uri = "mock:result")
	private MockEndpoint		to;

	private CouchDbClient		client;

	@Before
	public void before() {
		client = new CouchDbClient("camelcouchdb", true, "http", "localhost", 5984, null, null);
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from(from).to(to);
			}
		};
	}

	@Test
	public void testMessages() throws InterruptedException {
		final int messageCount = 100;
		to.expectedMessageCount(messageCount);

		// insert json manually into couch, camel will
		// then pick that up and send to mock endpoint
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int k = 0; k < messageCount; k++) {
					JsonElement obj = new Gson().toJsonTree("{ \"randomString\" : \"" + UUID.randomUUID() + "\" }");
					client.save(obj);
				}
			}
		});

		t.start();
		t.join();
		to.assertIsSatisfied();
	}
}
