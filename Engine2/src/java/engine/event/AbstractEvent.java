package engine.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rutils.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static engine.Engine.seconds;

abstract class AbstractEvent implements Event
{
    private static final Map<Class<? extends Event>, Set<Method>> METHOD_CACHE = new ConcurrentHashMap<>();
    
    private final double time;
    
    private Priority phase = null;
    
    AbstractEvent()
    {
        this.time = seconds();
    }
    
    @Override
    public String toString()
    {
        Set<Method> methods;
        synchronized (AbstractEvent.METHOD_CACHE)
        {
            methods = AbstractEvent.METHOD_CACHE.computeIfAbsent(getClass(), c -> ClassUtil.getMethods(c, m -> m.isAnnotationPresent(Property.class)));
        }
        
        StringBuilder s = new StringBuilder(getClass().getSimpleName().replace("_", "")).append("{");
        
        Iterator<Method> iterator = methods.iterator();
        while (true)
        {
            Method   method   = iterator.next();
            Property property = method.getAnnotation(Property.class);
            
            if (property.printName()) s.append(method.getName()).append('=');
            try
            {
                s.append(String.format(property.format(), method.invoke(this)));
            }
            catch (IllegalAccessException | InvocationTargetException ignored)
            {
                s.append(method.getReturnType());
            }
            
            if (iterator.hasNext())
            {
                s.append(", ");
            }
            else
            {
                break;
            }
        }
        return s.append("}").toString();
    }
    
    @Override
    public double time()
    {
        return this.time;
    }
    
    @Override
    public @Nullable Priority getPhase()
    {
        return this.phase;
    }
    
    @Override
    public void setPhase(@NotNull Priority value)
    {
        Objects.requireNonNull(value, "setPhase argument must not be null");
        int prev = this.phase == null ? -1 : this.phase.ordinal();
        if (prev >= value.ordinal()) throw new IllegalArgumentException("Attempted to set event phase to " + value + " when already " + this.phase);
        this.phase = value;
    }
}
