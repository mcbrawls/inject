package net.mcbrawls.inject.spring;

import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.jetty.JettyInjector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;

/**
 * Used to configure Jetty with Spring.
 */
public final class InjectWebServletFactory {
    private InjectWebServletFactory() {}

    /**
     * Creates a Jetty web server factory with port 0 (only used for logging purposes) and registers it to the given platform.
     * @param injectPlatform the platform
     * @return the factory
     */
    public static JettyServletWebServerFactory create(InjectPlatform injectPlatform, ObjectProvider<JettyServerCustomizer> serverCustomizers) {
        return create(injectPlatform, 0, serverCustomizers);
    }

    /**
     * Creates a Jetty web server factory and registers it to the given platform.
     * @param injectPlatform the platform
     * @param port the port to be used for logging
     * @return the factory
     */
    public static JettyServletWebServerFactory create(InjectPlatform injectPlatform, int port, ObjectProvider<JettyServerCustomizer> serverCustomizers) {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();

        // For logging reasons only. Does not have any actual effect during runtime
        factory.setPort(port);

        factory.addServerCustomizers(server -> {
            LocalConnector connector = new LocalConnector(server);

            // Replace builtin connectors with the local one
            server.setConnectors(new Connector[]{connector});

            injectPlatform.registerInjector(new JettyInjector(connector));
        });

        factory.getServerCustomizers().addAll(serverCustomizers.orderedStream().toList());

        return factory;
    }
}
