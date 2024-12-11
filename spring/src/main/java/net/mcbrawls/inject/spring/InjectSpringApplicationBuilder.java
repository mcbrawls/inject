package net.mcbrawls.inject.spring;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Properties;

/**
 * Utility for creating {@link org.springframework.boot.builder.SpringApplicationBuilder}s with inject settings.
 */
public final class InjectSpringApplicationBuilder {
    private InjectSpringApplicationBuilder() {}

    public static SpringApplicationBuilder create(Class<?> applicationClass, Properties props) {
        return new SpringApplicationBuilder(applicationClass)
                .resourceLoader(new DefaultResourceLoader(applicationClass.getClassLoader()))
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(true)
                .properties(props);
    }
}
