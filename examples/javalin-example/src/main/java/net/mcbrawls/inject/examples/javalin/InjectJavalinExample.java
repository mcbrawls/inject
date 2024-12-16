package net.mcbrawls.inject.examples.javalin;

import io.javalin.Javalin;
import net.mcbrawls.inject.javalin.InjectJavalinFactory;
import net.mcbrawls.inject.spigot.InjectSpigot;
import org.bukkit.plugin.java.JavaPlugin;

public class InjectJavalinExample extends JavaPlugin {
    private Javalin javalin;

    private void initJavalin() {
        javalin = InjectJavalinFactory.create(InjectSpigot.INSTANCE);
        javalin.get("/", (ctx -> {
            ctx.result("Hello from Javalin!");
        }));
        javalin.start();
    }

    @Override
    public void onEnable() {
        initJavalin();
    }

    @Override
    public void onDisable() {
        if (javalin != null) {
            javalin.stop();
        }
    }
}
