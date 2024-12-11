package net.mcbrawls.inject.javalin;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import net.mcbrawls.inject.api.InjectPlatform;
import net.mcbrawls.inject.jetty.JettyInjector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LocalConnector;

import java.util.function.Consumer;

/**
 * Factory for creating Javalin instances configured with Inject and Jetty.
 */
public final class InjectJavalinFactory {
    private InjectJavalinFactory() {}

    /**
     * Creates a Javalin instance and registers it for the given platform.
     * <p>
     * This uses the default settings:
     * <p>
     * <ul>
     * <li> uses virtual threads </li>
     * <li> no banner</li>
     * <li> configured jetty connector</li>
     * </ul>
     *
     * @param platform the inject platform to be used for registration
     * @return the javalin instance
     */
    public static Javalin create(InjectPlatform platform) {
        return create(platform, (ignored) -> {});
    }

    /**
     * Creates a Javalin instance and registers it for the given platform.
     * <p>
     * This uses the default settings and applies the given config on top:
     * <p>
     * <ul>
     * <li> uses virtual threads </li>
     * <li> no banner</li>
     * <li> configured jetty connector</li>
     * </ul>
     *
     * @param platform the inject platform to be used for registration
     * @param config the configuration
     * @return the javalin instance
     */
    public static Javalin create(InjectPlatform platform, Consumer<JavalinConfig> config) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.useVirtualThreads = true;
            javalinConfig.showJavalinBanner = false;
            javalinConfig.jetty.addConnector((server, httpConfig) -> {
                LocalConnector connector = new LocalConnector(server, new HttpConnectionFactory(httpConfig));
                platform.registerInjector(new JettyInjector(connector));

                return connector;
            });

            config.accept(javalinConfig);
        });
    }
}
