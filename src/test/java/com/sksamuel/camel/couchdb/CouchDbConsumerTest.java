package com.sksamuel.camel.couchdb;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.ExecutorServiceManager;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.lightcouch.Changes;
import org.lightcouch.CouchDbContext;
import org.lightcouch.CouchDbInfo;
import org.mockito.Mock;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 15:12:48
 * 
 */
public class CouchDbConsumerTest extends CamelTestSupport {

	@EndpointInject(uri = "direct:start")
	private Endpoint				from;

	@SuppressWarnings("hiding")
	@Produce(uri = "direct:start")
	protected ProducerTemplate		template;

	@EndpointInject(uri = "mock:result")
	private MockEndpoint			to;

	@Mock
	private Processor				processor;

	@Mock
	private CouchDbClientWrapper		client;

	@Mock
	private CouchDbEndpoint			endpoint;

	@SuppressWarnings("hiding")
	private CouchDbConsumer			consumer;

	@Mock
	private CamelContext			ctx;

	@Mock
	private ExecutorServiceManager	manager;

	@Mock
	private ExecutorService			executor;

	@Mock
	private CouchDbContext			couchContext;

	@Mock
	private CouchDbInfo			info;

	@Mock
	private Changes				changes;

	@Before
	public void before() {
		initMocks(this);

		when(client.context()).thenReturn(couchContext);
		when(couchContext.info()).thenReturn(info);
		when(info.getUpdateSeq()).thenReturn("100");

		when(client.changes()).thenReturn(changes);
		when(changes.continuousChanges()).thenReturn(changes);
		when(changes.includeDocs(true)).thenReturn(changes);
		when(changes.since(anyString())).thenReturn(changes);
		when(changes.heartBeat(anyLong())).thenReturn(changes);

		consumer = new CouchDbConsumer(endpoint, client, processor);
		when(endpoint.getCamelContext()).thenReturn(ctx);
		when(ctx.getExecutorServiceManager()).thenReturn(manager);
		when(manager.newFixedThreadPool(any(CouchDbConsumer.class), anyString(), anyInt())).thenReturn(executor);
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from(from).process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {

					}
				}).to(to);
			}
		};
	}

	@Test
	public void test1000VirtualMessages() throws Exception {
		// just test that the messages are passed on into the final endpoint ok
		to.expectedMessageCount(1000);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 1; i <= 1000; i++) {
					template.sendBody("test");
				}
			}
		});

		t.start();
		t.join();
		to.assertIsSatisfied();
	}

	@Test
	public void testExecutorLifecycleForStopStart() throws Exception {
		consumer.doStart();
		verify(executor, never()).shutdownNow();

		consumer.doStop();
		verify(executor).shutdownNow();

		// 2nd invocation should have no effect
		consumer.doStop();
		verify(executor).shutdownNow();
	}

	@Test
	public void testStopInterruptsExecutor() throws Exception {
		ExecutorService realexe = Executors.newSingleThreadExecutor();
		Future future = realexe.submit(new Runnable() {

			@Override
			public void run() {
				BlockingQueue queue = new ArrayBlockingQueue(10);
				try {
					queue.take();
					fail("We were not interrupted");
				} catch (InterruptedException e) {
				}
			}

		});
		when(manager.newFixedThreadPool(any(CouchDbConsumer.class), anyString(), anyInt())).thenReturn(realexe);
		consumer.doStart();
		consumer.doStop();
		future.get();
		assertTrue(realexe.isShutdown());
		assertTrue(realexe.isTerminated());
	}

	@Test
	public void testTrackingTaskIsSubmitted() throws Exception {
		consumer.doStart();
		verify(executor).submit(any(Runnable.class));
	}
}
