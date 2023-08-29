package auctionsniper.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class Announcer<T extends EventListener> {
    private final T proxy;
    List<T> listeners = new ArrayList<>();

    public Announcer(Class<? extends T> listenerType) {
        var proxyInstance = Proxy.newProxyInstance(
                listenerType.getClassLoader(),
                new Class<?>[]{listenerType},
                this::invokeAllListeners
        );

        this.proxy = listenerType.cast(proxyInstance);
    }

    private Object invokeAllListeners(Object _proxy, Method method, Object[] args) {
        try {
            for (T listener : listeners) {
                method.invoke(listener, args);
            }
            return null;
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("could not invoke listener", e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            else if (cause instanceof Error) {
                throw (Error)cause;
            }
            else {
                throw new UnsupportedOperationException("listener threw exception", cause);
            }
        }
    }

    public static <T extends EventListener> Announcer<T> to(Class<T> listenerType) {
        return new Announcer<>(listenerType);
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public T announce() {
        return proxy;
    }
}
