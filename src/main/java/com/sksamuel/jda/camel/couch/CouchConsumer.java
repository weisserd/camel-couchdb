package com.sksamuel.jda.camel.couch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephen K Samuel samspade79@gmail.com 21 May 2012 07:05:49
 * 
 */
public class CouchConsumer extends DefaultConsumer {

	private static final Logger	logger	= LoggerFactory.getLogger(CouchConsumer.class);

	private final Processor		processor;
	private final CouchDbClient	couchClient;
	private final CouchEndpoint	endpoint;
	private ExecutorService		executor;
	private CouchChangesTracker	task;

	public CouchConsumer(CouchDbClient couchClient, CouchEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
		this.couchClient = couchClient;
		this.endpoint = endpoint;
		this.processor = processor;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		executor = endpoint.getCamelContext().getExecutorServiceManager().newFixedThreadPool(this, endpoint.getEndpointUri(), 1);
		task = new CouchChangesTracker(endpoint, this, couchClient);
		executor.submit(task);
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (task != null) {
			task.stop();
		}
		if (executor != null) {
			executor.shutdown();
			executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
			if (!executor.isShutdown()) {
				executor.shutdownNow();
			}
			executor = null;
		}
	}
}
