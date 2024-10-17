# inject
Inject is a simple server-side mod to allow developers to inject into
Netty easier.

## Example
This uses the `HttpInjector` class to respond to HTTP requests to the Minecraft
server.

```kt
object MyEpicHttpInjector : HttpInjector() {
    override fun isRelevant(ctx: InjectorContext, request: HttpRequest) = true
    override fun intercept(ctx: ChannelHandlerContext, request: HttpRequest) = ctx.buildHttpBuffer {
        writeStatusLine("1.1", 200, "OK")
        writeText("Hello, from Minecraft!")
    }
}

object MyMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        MyEpicHttpInjector.register()
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
```groovy
repositories {
    maven {
        name = "Andante"
        url  = "https://maven.andante.dev/releases/"
    }
}
```

Add the dependency:
```groovy
dependencies {
    include modImplementation("net.mcbrawls:inject:VERSION")
}
```

Replace `VERSION` with the latest version from the releases tab.