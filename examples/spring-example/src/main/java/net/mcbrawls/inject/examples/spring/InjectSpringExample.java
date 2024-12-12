package net.mcbrawls.inject.examples.spring;

import net.mcbrawls.inject.spring.InjectSpringApplicationBuilder;
import net.mcbrawls.inject.spring.InjectSpringApplicationLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;
import java.util.logging.Level;

public class InjectSpringExample extends JavaPlugin {
  private ConfigurableApplicationContext applicationContext;

  private void initSpring() {
    try {
      Properties props = new Properties();
      props.load(this.getResource("application.properties"));
      applicationContext = InjectSpringApplicationLoader
          .run(InjectSpringApplicationBuilder.create(InjectSpringApplication.class, props));
    } catch (Exception exception) {
      getLogger().log(Level.SEVERE, "Spring error, shutting down...");
      exception.printStackTrace();
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onEnable() {
    initSpring();
  }

  @Override
  public void onDisable() {
    if (applicationContext != null) {
      applicationContext.close();
    }
  }
}
