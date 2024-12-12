# inject
Inject is a simple server-side library to allow developers to inject into Netty easier.

## Example
This uses the `HttpInjector` class to respond to HTTP requests to the Minecraft
server.

```java
class MyEpicHttpInjector extends HttpInjector {
    @Override
    public HttpByteBuf intercept(ChannelHandlerContext ctx, HttpRequest request) {
        HttpByteBuf buf = HttpByteBuf.httpBuf(ctx);
        buf.writeStatusLine("1.1", 200, "OK");
        buf.writeText("Hello, from Minecraft!");
        return buf;
    }
}
```

## Registration
For Fabric, use the `InjectFabric` class:
```java
public class MyMod implements ModInitializer {
    @Override
    public void onInitialize() {
        InjectFabric.INSTANCE.registerInjector(new MyEpicHttpInjector());
    }
}
```

For Spigot, Paper and derivatives, use the `InjectSpigot` class:
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        InjectSpigot.INSTANCE.registerInjector(new MyEpicHttpInjector());
    }
}
```

This will register an HTTP injector which will respond with `Hello, from Minecraft!`
to any HTTP request to the Minecraft port.

```bash
$ curl http://localhost:25565
Hello, from Minecraft!
```

Further examples like Spring and Javalin can be found in the `examples` module.

## Usage
Add the andante repo to gradle:
```kt
repositories {
    maven("https://maven.andante.dev/releases/")
}
```

Add the dependency:
```kt
dependencies {
    implementation("net.mcbrawls.inject:api:VERSION")
 
    // HTTP-related things:
    implementation("net.mcbrawls.inject:http:VERSION")

    // Fabric:
    include(modImplementation("net.mcbrawls.inject:fabric:VERSION")!!)
 
    // Spigot/Paper:
    implementation("net.mcbrawls.inject:spigot:VERSION")
}
```

Replace `VERSION` with the latest version from the releases tab.
