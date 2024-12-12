package net.mcbrawls.inject.examples.spring;

import net.mcbrawls.inject.spigot.InjectSpigot;
import net.mcbrawls.inject.spring.InjectWebServletFactory;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InjectSpringApplication {
    @Bean
    JettyServletWebServerFactory servletWebServerFactory(ObjectProvider<JettyServerCustomizer> serverCustomizers) {
        return InjectWebServletFactory.create(InjectSpigot.INSTANCE, Bukkit.getPort(), serverCustomizers);
    }
}
