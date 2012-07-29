package com.sksamuel.camel.couchdb;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.lightcouch.Response;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 15:57:45
 * 
 */
public class CouchDbProducerTest {

	@Mock
	private CouchDbClientWrapper	client;

	@Mock
	private CouchDbEndpoint		endpoint;

	@Mock
	private Exchange			exchange;

	@Mock
	private Message			msg;

	private CouchDbProducer		producer;

	@Before
	public void before() {
		initMocks(this);
		producer = new CouchDbProducer(endpoint, client);
		when(exchange.getIn()).thenReturn(msg);
	}

	@Test(expected = CouchDbException.class)
	public void testBodyMandatory() throws Exception {
		when(msg.getMandatoryBody()).thenReturn(null);
		producer.process(exchange);
	}

	@Test
	public void testDocumentHeadersAreSet() throws Exception {

		String id = UUID.randomUUID().toString();
		String rev = UUID.randomUUID().toString();

		Response response = new Response();
		response.setId(id);
		response.setRev(rev);

		JsonObject doc = new JsonObject();
		doc.addProperty("_id", id);
		doc.addProperty("_rev", rev);

		when(msg.getMandatoryBody()).thenReturn(doc);
		when(client.update(doc)).thenReturn(response);

		producer.process(exchange);
		verify(msg).setHeader(CouchDbEndpoint.HEADER_DOC_ID, id);
		verify(msg).setHeader(CouchDbEndpoint.HEADER_DOC_REV, rev);
	}

	@Test(expected = CouchDbException.class)
	public void testNullSaveResponseThrowsError() throws Exception {
		when(client.save(anyObject())).thenReturn(null);
		producer.process(exchange);
	}

	@Test
	public void testStringBodyIsConvertedToJsonTree() throws Exception {

		when(msg.getMandatoryBody()).thenReturn("{ \"name\" : \"coldplay\" }");
		when(client.save(anyObject())).thenAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				assertTrue(invocation.getArguments()[0].getClass() + " but wanted " + JsonObject.class,
						invocation.getArguments()[0] instanceof JsonElement);
				return new Response();
			}
		});
		producer.process(exchange);
		verify(client).save(any(JsonObject.class));
	}
}
