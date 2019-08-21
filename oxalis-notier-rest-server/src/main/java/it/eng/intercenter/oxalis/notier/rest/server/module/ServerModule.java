package it.eng.intercenter.oxalis.notier.rest.server.module;

import com.google.inject.servlet.ServletModule;

import it.eng.intercenter.oxalis.notier.rest.server.servlet.OutboundServlet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Manuel Gozzi
 * @date 21 ago 2019
 * @time 12:27:00
 */
@Slf4j
public class ServerModule extends ServletModule {

	private static final String SERVER_SERVLET_PATH = "/sendOutbound";

	@Override
	protected void configureServlets() {
		super.configureServlets();

		log.info("Binding {} as eager singleton", OutboundServlet.class.getName());
		bind(OutboundServlet.class).asEagerSingleton();

		log.info("Serve {} with {}", SERVER_SERVLET_PATH, OutboundServlet.class.getName());
		serve(SERVER_SERVLET_PATH).with(OutboundServlet.class);
	}

}
