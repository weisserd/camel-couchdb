package com.sksamuel.camel.couchdb;

import java.util.concurrent.ExecutorService;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:49
 * 
 */
public class CouchDbConsumer extends DefaultConsumer {

	private static final Logger		logger	= LoggerFactory.getLogger(CouchDbConsumer.class);

	private final Processor			processor;
	private final CouchDbClient		couchClient;
	private final CouchDbEndpoint		endpoint;
	private ExecutorService			executor;
	private CouchDbChangesetTracker	task;

	public CouchDbConsumer(CouchDbEndpoint endpoint, CouchDbClient couchClient, Processor processor) {
		super(endpoint, processor);
		this.couchClient = couchClient;
		this.endpoint = endpoint;
		this.processor = processor;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		logger.info("Starting CouchDB consumer");
		executor = endpoint.getCamelContext().getExecutorServiceManager().newFixedThreadPool(this, endpoint.getEndpointUri(), 1);
		task = new CouchDbChangesetTracker(endpoint, this, couchClient);
		executor.submit(task);
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		logger.info("Stopping CouchDB consumer");
		if (task != null) {
			task.stop();
		}
		if (executor != null) {
			if (!executor.isShutdown()) {
				executor.shutdownNow();
			}
			executor = null;
		}
	}

	@Override
	public void resume() throws Exception {
		super.resume();
		doStart();
	}

	@Override
	public void suspend() throws Exception {
		super.suspend();
		// suspend can do a stop as couch is a stateless protocol. There is no overload to be saved by maintaining a
		// "connection" as in other components where suspend and resume are used to avoid costly setups
		doStop();
	}
}
