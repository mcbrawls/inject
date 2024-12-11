package net.mcbrawls.inject.examples.paper;

import net.mcbrawls.inject.paper.InjectPaper;
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
        System.out.println("Hello");
        return InjectWebServletFactory.create(InjectPaper.INSTANCE, Bukkit.getPort(), serverCustomizers);
    }
}
