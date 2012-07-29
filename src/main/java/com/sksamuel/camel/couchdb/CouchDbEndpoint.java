package com.sksamuel.camel.couchdb;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:10
 * 
 */
public class CouchDbEndpoint extends DefaultEndpoint {

	public static final String	HEADER_DATABASE	= "CouchDbDatabase";
	public static final String	HEADER_SEQ		= "CouchDbSeq";
	public static final String	HEADER_DOC_ID	= "CouchDbId";
	public static final String	HEADER_DOC_REV	= "CouchDbRev";

	private static final String	URI_ERROR		= "Invalid URI. Format must be of the form couchdb:http[s]://hostname[:port]/database?[options...]";
	static final long			DEFAULT_HEARTBEAT	= 30000;
	static final int			DEFAULT_PORT	= 5984;

	private final String		protocol;

	private final String		hostname;

	private String			username;

	private final String		database;

	private String			password;

	private final int			port;

	private long			heartbeart		= DEFAULT_HEARTBEAT;

	private boolean			createDatabase;

	public CouchDbEndpoint(String endpointUri, String remaining, CouchDbComponent component) throws URISyntaxException {
		super(endpointUri, component);

		URI uri = new URI(remaining);

		protocol = uri.getScheme();
		if (protocol == null)
			throw new CouchDbException(URI_ERROR);

		port = uri.getPort() == -1 ? DEFAULT_PORT : uri.getPort();

		if (uri.getPath() == null || uri.getPath().trim().length() == 0)
			throw new CouchDbException(URI_ERROR);
		database = uri.getPath().substring(1);

		hostname = uri.getHost();
		if (hostname == null)
			throw new CouchDbException(URI_ERROR);
	}

	CouchDbClientWrapper createClient() {
		return new CouchDbClientWrapper(new CouchDbClient(database, createDatabase, protocol, hostname, port, username, password));
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new CouchDbConsumer(this, createClient(), processor);
	}

	public Exchange createCouchExchange(String seq, String id, JsonObject obj) {
		Exchange exchange = new DefaultExchange(getCamelContext(), getExchangePattern());

		Message message = new DefaultMessage();
		message.setHeader(HEADER_DATABASE, database);
		message.setHeader(HEADER_SEQ, seq);
		message.setHeader(HEADER_DOC_ID, id);
		message.setHeader(HEADER_DOC_REV, obj.get("_rev").getAsString());

		message.setBody(obj);
		exchange.setIn(message);

		return exchange;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new CouchDbProducer(this, createClient());
	}

	public long getHeartbeart() {
		return heartbeart;
	}

	public int getPort() {
		return port;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setCreateDatabase(boolean create) {
		this.createDatabase = create;
	}

	public void setHeartbeart(long heartbeart) {
		this.heartbeart = heartbeart;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
