package engine.event;

import rutils.ClassUtil;
import rutils.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class EventBus
{
    private static final Logger LOGGER = new Logger();
    
    private static final Map<Integer, IEventListener>                       wrappedCache       = new HashMap<>();
    private static final Map<Object, List<IEventListener>>                  objectListeners    = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<IEventListener>>                 eventListeners     = new ConcurrentHashMap<>();
    private static final Map<Priority, Map<Class<?>, List<IEventListener>>> classListenersMaps = new ConcurrentHashMap<>();
    
    private static boolean shutdown = false;
    
    static
    {
        for (Priority priority : Priority.values()) EventBus.classListenersMaps.put(priority, new HashMap<>());
    }
    
    private EventBus() {}
    
    public static void start()
    {
        EventBus.LOGGER.fine("EventBus starting.");
    
        EventBus.shutdown = false;
    }
    
    public static void shutdown()
    {
        EventBus.LOGGER.fine("EventBus stopping.");
        
        EventBus.shutdown = true;
    }
    
    public static void register(final Object target)
    {
        if (EventBus.objectListeners.containsKey(target)) return;
        
        if (target.getClass() == Class.class)
        {
            registerClass((Class<?>) target);
        }
        else
        {
            registerObject(target);
        }
    }
    
    public static void unregister(final Object target)
    {
        List<IEventListener> toRemove = EventBus.objectListeners.remove(target);
        
        if (toRemove == null) return;
        for (Map<Class<?>, List<IEventListener>> classListenersMap : EventBus.classListenersMaps.values())
        {
            for (List<IEventListener> classListeners : classListenersMap.values())
            {
                classListeners.removeAll(toRemove);
            }
        }
    }
    
    public static void post(Event event)
    {
        if (EventBus.shutdown) return;
        
        EventBus.LOGGER.finest("Posting", event);
        
        Set<IEventListener> listeners = EventBus.eventListeners.computeIfAbsent(event.getClass(), EventBus::computeListeners);
        
        int index = 0;
        try
        {
            for (IEventListener listener : listeners)
            {
                if (Objects.equals(listener.getClass(), Priority.class)) continue;
                listener.invoke(event);
                index++;
            }
        }
        catch (Throwable throwable)
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Exception caught during firing event: ").append(throwable.getMessage()).append('\n');
            builder.append("\tIndex: ").append(index).append('\n');
            builder.append("\tListeners:\n");
            index = 0;
            for (IEventListener listener : listeners) builder.append("\t\t").append(index++).append(": ").append(listener).append('\n');
            final StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            builder.append(sw.getBuffer());
            EventBus.LOGGER.severe(builder.toString());
            throw throwable;
        }
    }
    
    private static void registerClass(final Class<?> clazz)
    {
        for (Method m : ClassUtil.getMethods(clazz, m -> m.getDeclaringClass() != Object.class && Modifier.isStatic(m.getModifiers())))
        {
            if (m.isAnnotationPresent(Subscribe.class))
            {
                registerListener(clazz, m);
            }
        }
    }
    
    private static void registerObject(final Object obj)
    {
        for (Method m : ClassUtil.getMethods(obj.getClass(), m -> m.getDeclaringClass() != Object.class && !Modifier.isStatic(m.getModifiers()) && m.canAccess(obj)))
        {
            if (m.isAnnotationPresent(Subscribe.class))
            {
                registerListener(obj, m);
            }
        }
    }
    
    private static void registerListener(final Object target, final Method method)
    {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1)
        {
            throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation. " +
                                               "It has " + parameterTypes.length + " arguments, " +
                                               "but event handler methods require a single argument only."
            );
        }
        
        Class<?> eventType = parameterTypes[0];
        
        if (!Event.class.isAssignableFrom(eventType))
        {
            throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, " +
                                               "but takes an argument that is not an Event subtype : " + eventType);
        }
        
        addToListeners(target, eventType, wrapMethod(target, method), method.getAnnotation(Subscribe.class).priority());
    }
    
    private static void addToListeners(final Object target, final Class<?> eventType, final IEventListener listener, final Priority priority)
    {
        EventBus.LOGGER.finer("Adding listener '%s' of '%s' to target '%s' with priority=%s", listener, eventType.getSimpleName(), target, priority);
        
        List<IEventListener> objectListeners = EventBus.objectListeners.computeIfAbsent(target, c -> Collections.synchronizedList(new ArrayList<>()));
        objectListeners.add(listener);
        
        Map<Class<?>, List<IEventListener>> classListenersMap = EventBus.classListenersMaps.get(priority);
        
        EventBus.eventListeners.clear();
        
        List<IEventListener> classListeners = classListenersMap.computeIfAbsent(eventType, c -> Collections.synchronizedList(new ArrayList<>()));
        classListeners.add(listener);
    }
    
    private static IEventListener wrapMethod(final Object target, final Method method)
    {
        int hash = Objects.hash(target, method.getName(), Arrays.hashCode(method.getParameterTypes()));
        return EventBus.wrappedCache.computeIfAbsent(hash, h -> event -> {
            try
            {
                method.invoke(target.getClass() == Class.class ? null : target, event);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                EventBus.LOGGER.severe("Could not access listener method.");
                EventBus.LOGGER.severe(e);
            }
        });
    }
    
    private static Set<IEventListener> computeListeners(final Class<?> eventClass)
    {
        Set<Class<?>> classes  = ClassUtil.getTypes(eventClass, clz -> Modifier.isInterface(clz.getModifiers()));
        Class<?>[]    classArr = classes.toArray(new Class<?>[0]);
        
        Set<IEventListener> listeners = new LinkedHashSet<>();
        for (Priority priority : Priority.values())
        {
            Map<Class<?>, List<IEventListener>> classListenersMap = EventBus.classListenersMaps.get(priority);
            
            boolean toAdd = true;
            for (int i = classArr.length - 1; i >= 0; i--)
            {
                List<IEventListener> eventListeners = classListenersMap.get(classArr[i]);
                
                if (eventListeners != null)
                {
                    if (toAdd)
                    {
                        listeners.add(priority);
                        toAdd = false;
                    }
                    listeners.addAll(eventListeners);
                }
            }
        }
        return listeners;
    }
    
    @Retention(value = RUNTIME)
    @Target(value = METHOD)
    public @interface Subscribe
    {
        Priority priority() default Priority.NORMAL;
    }
}
