package net.mcbrawls.inject.examples.paper;

import io.javalin.Javalin;
import io.netty.channel.ChannelHandlerContext;
import net.mcbrawls.inject.http.HttpByteBuf;
import net.mcbrawls.inject.http.HttpInjector;
import net.mcbrawls.inject.http.HttpRequest;
import net.mcbrawls.inject.javalin.InjectJavalinFactory;
import net.mcbrawls.inject.paper.InjectPaper;
import net.mcbrawls.inject.spring.InjectSpringApplicationBuilder;
import net.mcbrawls.inject.spring.InjectSpringApplicationLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;
import java.util.logging.Level;

public class InjectPaperExample extends JavaPlugin {
    private Javalin javalin;
    private ConfigurableApplicationContext applicationContext;

    static class MyEpicHttpInjector extends HttpInjector {
        @Override
        public HttpByteBuf intercept(ChannelHandlerContext ctx, HttpRequest request) {
            HttpByteBuf buf = HttpByteBuf.httpBuf(ctx);
            buf.writeStatusLine("1.1", 200, "OK");
            buf.writeText("Hello, from Minecraft!");
            return buf;
        }
    }

    private void initJavalin() {
        javalin = InjectJavalinFactory.create(InjectPaper.INSTANCE);
        javalin.get("/", (ctx -> {
            ctx.result("Hello from Javalin!");
        }));
        javalin.start();
    }

    private void initSpring() {
        try {
            Properties props = new Properties();
            props.load(this.getResource("application.properties"));
            applicationContext = InjectSpringApplicationLoader.run(InjectSpringApplicationBuilder.create(InjectSpringApplication.class, props));
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Spring error, shutting down...");
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        // InjectPaper.INSTANCE.registerInjector(new MyEpicHttpInjector());
        // initJavalin()
        initSpring();
    }

    @Override
    public void onDisable() {
        if (javalin != null) {
            javalin.stop();
        }

        if (applicationContext != null) {
            applicationContext.close();
        }
    }
}
