package net.mcbrawls.inject.velocity;

import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

final class Reflection {
    private Reflection() {}

    public static final Class<Object> CONNECTION_MANAGER = getClass("com.velocitypowered.proxy.network.ConnectionManager");
    public static final Class<Object> SERVER_INITIALIZER_HOLDER = getClass("com.velocitypowered.proxy.network.ServerChannelInitializerHolder");
    public static final Method SET_SERVER_INITIALIZER = getMethod(SERVER_INITIALIZER_HOLDER, 0, ChannelInitializer.class);

    static <T, R> R getField(T instance, Class<? extends T> clazz, int idx, Class<R> type) {
        Field declaredField = Arrays
                .stream(clazz.getDeclaredFields())
                .filter((field) -> field.getType().equals(type))
                .toList()
                .get(idx);
        declaredField.setAccessible(true);

        try {
            //noinspection unchecked
            return (R) declaredField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getMethod(Class<?> clazz, int idx, Class<?>... params) {
        if (clazz == null) {
            return null;
        }
        int currentIdx = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            if ((params == null || Arrays.equals(method.getParameterTypes(), params)) && idx == currentIdx++) {
                method.setAccessible(true);
                return method;
            }
        }
        if (clazz.getSuperclass() != null) {
            return getMethod(clazz.getSuperclass(), idx, params);
        }
        return null;
    }

    private static Class<Object> getClass(String name) {
        try {
            //noinspection unchecked
            return (Class<Object>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed getting class: " + name);
        }
    }
}
