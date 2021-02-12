package engine.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Event
{
    @Property(format = "%.3f")
    double time();
    
    @Nullable
    Priority getPhase();
    
    void setPhase(@NotNull Priority value);
}
