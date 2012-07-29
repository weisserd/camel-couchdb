package com.sksamuel.jda.camel.couch;

import org.apache.camel.Exchange;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 13:29:38
 * 
 */
public class CouchChangesTracker implements Runnable {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchChangesTracker.class);

	private volatile boolean	run		= true;

	private volatile boolean	stopped;

	private final CouchDbClient	couchClient;

	private Changes			changes;

	private final CouchEndpoint	endpoint;

	private final CouchConsumer	consumer;

	public CouchChangesTracker(CouchEndpoint endpoint, CouchConsumer consumer, CouchDbClient couchClient) {
		this.endpoint = endpoint;
		this.consumer = consumer;
		this.couchClient = couchClient;
		initChanges();
	}

	void initChanges() {
		CouchDbInfo dbInfo = couchClient.context().info();
		String since = dbInfo.getUpdateSeq(); // get latest update seq
		logger.debug("Last sequence [{}]", since);
		changes = couchClient.changes().includeDocs(true).since(since).heartBeat(endpoint.getHeartbeart()).continuousChanges();
	}

	@Override
	public void run() {
		while (changes.hasNext()) { // blocks until a feed is received

			ChangesResult.Row feed = changes.next();
			JsonObject doc = feed.getDoc();

			try {

				Exchange exchange = endpoint.createCouchExchange(doc.toString());
				logger.trace("Sending exchange: {}, _id: {}", exchange, doc.get("_id"));
				consumer.getProcessor().process(exchange);

			} catch (Exception e) {
			}
		}
		stopped = true;
	}

	public void stop() {
		changes.stop();
		while (!stopped) {
			logger.info("Couch consuming thread shutdown");
		}
	}
}
