package com.sksamuel.camel.couchdb;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @author Stephen K Samuel samspade79@gmail.com 29 Jul 2012 15:12:48
 * 
 */
public class CouchDbConsumerTest extends CamelTestSupport {

	@EndpointInject(uri = "direct:start")
	private Endpoint			from;

	@SuppressWarnings("hiding")
	@Produce(uri = "direct:start")
	protected ProducerTemplate	template;

	@EndpointInject(uri = "mock:result")
	private MockEndpoint		to;

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
	public void test1000Messages() throws Exception {
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
}
