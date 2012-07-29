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

	public static final String	DATABASE		= "CouchDbDatabase";
	public static final String	HOSTNAME		= "CouchDbHostname";
	public static final String	ID			= "CouchDbId";
	public static final String	REV			= "CouchDbRev";

	private static final int	DEFAULT_PORT	= 5984;
	private static final String	URI_ERROR		= "Invalid URI. Format must be of the form couchdb:http[s]://hostname[:port]/database?[options...]";
	private static final long	DEFAULT_HEARTBEAT	= 30000;

	private String			protocol		= "http";

	private final String		hostname;

	private String			username;

	private String			database;

	private String			password;

	private int				port;

	private long			heartbeart		= DEFAULT_HEARTBEAT;

	private boolean			createDatabase;

	public CouchDbEndpoint(String endpointUri, String remaining, CouchDbComponent component) throws URISyntaxException {
		super(endpointUri, component);

		URI uri = new URI(remaining);

		protocol = uri.getScheme();
		if (protocol == null)
			throw new CouchDbException(URI_ERROR);

		port = uri.getPort();
		if (port == -1)
			port = DEFAULT_PORT;

		database = uri.getPath();
		if (database == null)
			throw new CouchDbException(URI_ERROR);

		hostname = uri.getHost();
		if (hostname == null)
			throw new CouchDbException(URI_ERROR);
	}

	CouchDbClient createClient() {
		return new CouchDbClient(database, createDatabase, protocol, hostname, port, username, password);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new CouchDbConsumer(this, createClient(), processor);
	}

	public Exchange createCouchExchange(JsonObject obj) {
		Exchange exchange = new DefaultExchange(this.getCamelContext(), getExchangePattern());

		Message message = new DefaultMessage();
		message.setHeader(DATABASE, database);
		message.setHeader(HOSTNAME, hostname);
		message.setHeader(ID, obj.get("_id").getAsString());
		message.setHeader(REV, obj.get("_rev").getAsString());

		message.setBody(obj);
		exchange.setIn(message);

		return exchange;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new CouchDbProducer(this, createClient());
	}

	public String getDatabase() {
		return database;
	}

	public long getHeartbeart() {
		return heartbeart;
	}

	public String getHostname() {
		return hostname;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUsername() {
		return username;
	}

	public boolean isCreateDatabase() {
		return createDatabase;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setCreateDatabase(boolean create) {
		this.createDatabase = create;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setHeartbeart(long heartbeart) {
		this.heartbeart = heartbeart;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
