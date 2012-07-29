package com.sksamuel.jda.camel.couch;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.lightcouch.CouchDbClient;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:10
 * 
 */
public class CouchEndpoint extends DefaultEndpoint {

	public static final String	DATABASE	= "CouchDbDatabase";
	public static final String	HOSTNAME	= "CouchDbHostname";

	private String			protocol	= "http";

	private String			hostname	= "localhost";

	private String			database;

	private String			username;

	private String			password;

	private int				port		= 80;

	private long			heartbeart	= 30000;

	private boolean			create	= true;

	private final CouchDbClient	couchClient;

	public CouchEndpoint() {
		couchClient = new CouchDbClient(database, create, protocol, hostname, port, username, password);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new CouchConsumer(couchClient, this, processor);
	}

	public Exchange createCouchExchange(String doc) {
		Exchange exchange = new DefaultExchange(this.getCamelContext(), getExchangePattern());

		Message message = new DefaultMessage();
		message.setHeader(DATABASE, database);
		message.setHeader(HOSTNAME, hostname);

		message.setBody(doc);
		exchange.setIn(message);

		return exchange;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new CouchProducer(couchClient, this);
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

	public boolean isCreate() {
		return create;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setHeartbeart(long heartbeart) {
		this.heartbeart = heartbeart;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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
