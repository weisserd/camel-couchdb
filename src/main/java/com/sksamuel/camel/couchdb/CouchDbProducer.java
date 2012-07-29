package com.sksamuel.camel.couchdb;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.impl.DefaultProducer;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:44
 * 
 */
public class CouchDbProducer extends DefaultProducer {

	private static final Logger		logger	= LoggerFactory.getLogger(CouchDbProducer.class);

	private final CouchDbEndpoint		endpoint;
	private final CouchDbClientWrapper	couchClient;

	public CouchDbProducer(CouchDbEndpoint endpoint, CouchDbClientWrapper couchClient) {
		super(endpoint);
		this.couchClient = couchClient;
		this.endpoint = endpoint;
	}

	JsonElement getBodyAsJsonElement(Exchange exchange) throws InvalidPayloadException {
		Object body = exchange.getIn().getMandatoryBody();
		if (body instanceof String)
			return new Gson().toJsonTree(body);
		else if (body instanceof JsonElement)
			return (JsonElement) body;
		else
			throw new CouchDbException("Unsupported body type: " + body);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		try {

			JsonElement json = getBodyAsJsonElement(exchange);
			Response save = saveJsonElement(json);
			if (save == null)
				throw new CouchDbException("Could not save document [unknown reason]");

			logger.trace("Document saved [_id={}, _rev={}]", save.getId(), save.getRev());
			exchange.getIn().setHeader(CouchDbEndpoint.HEADER_DOC_REV, save.getRev());
			exchange.getIn().setHeader(CouchDbEndpoint.HEADER_DOC_ID, save.getId());

		} catch (Exception e) {
			throw new CouchDbException(e);
		}
	}

	Response saveJsonElement(JsonElement json) {
		Response save;
		if (json instanceof JsonObject) {
			JsonObject obj = (JsonObject) json;
			if (obj.get("_rev") == null)
				save = couchClient.save(json);
			else
				save = couchClient.update(json);
		} else {
			save = couchClient.save(json);
		}
		return save;
	}
}
