package net.mcbrawls.inject.spring;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loads spring applications.
 */
public final class InjectSpringApplicationLoader {
    private InjectSpringApplicationLoader() {}

    /**
     * Sets up and runs a Spring application.
     * @param builder the spring application builder
     * @return the application context
     * @throws Exception any exception that occurs inside of Spring
     */
    public static ConfigurableApplicationContext run(SpringApplicationBuilder builder) throws Exception {
        if (!Objects.equals(System.getProperty(LoggingSystem.SYSTEM_PROPERTY), LoggingSystem.NONE)) {
            System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LoggingSystem.NONE);
        }

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            ClassLoader loader = builder.application().getClassLoader();

            return executor.submit(() -> {
                Thread.currentThread().setContextClassLoader(loader);

                return builder.run();
            }).get();
        }
    }

    /**
     * Sets up and runs a Spring application using {@link InjectSpringApplicationBuilder}.
     * @param applicationClass the application class
     * @return the application context
     * @throws Exception any exception that occurs inside of Spring
     */
    public static ConfigurableApplicationContext run(Class<?> applicationClass) throws Exception {
        Properties props = new Properties();
        InputStream propsStream = applicationClass.getClassLoader().getResourceAsStream("/application.properties");

        if (propsStream == null) {
            throw new Exception("application.properties does not exist!");
        }

        props.load(propsStream);

        return run(InjectSpringApplicationBuilder.create(applicationClass, props));
    }
}
