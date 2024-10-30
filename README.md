# inject
Inject is a simple server-side library to allow developers to inject into Netty easier.

## Example
This uses the `HttpInjector` class to respond to HTTP requests to the Minecraft
server.

```java
class MyEpicHttpInjector extends HttpInjector {
    @Override
    public boolean isRelevant(InjectorContext ctx, HttpRequest request) {
        return true;
    }
    
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

For Paper, use the `InjectPaper` class:
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        InjectPaper.INSTANCE.registerInjector(new MyEpicHttpInjector());
    }
}
```

This will register an HTTP injector which will respond with `Hello, from Minecraft!`
to any HTTP request to the Minecraft port.

```bash
$ curl http://localhost:25565
Hello, from Minecraft!
```

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
    // Fabric:
    include(modImplementation("net.mcbrawls.inject:fabric:VERSION")!!)
    
    // Paper:
    implementation("net.mcbrawls.inject:paper:VERSION")
}
```

Replace `VERSION` with the latest version from the releases tab.