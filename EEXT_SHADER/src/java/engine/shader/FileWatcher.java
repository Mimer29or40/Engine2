package engine.shader;

import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static engine.util.Util.getPath;
import static engine.util.Util.println;

public class FileWatcher extends Thread
{
    private final AtomicBoolean stop    = new AtomicBoolean(false);
    private final AtomicBoolean changed = new AtomicBoolean(true);
    
    private final Path filePath;
    
    public FileWatcher(String file)
    {
        super(null, null, "ShaderFileWatcher", 0);
        
        this.filePath = getPath(file);
        
        setDaemon(true);
        start();
    }
    
    public void stopThread()
    {
        this.stop.set(true);
    }
    
    public boolean fileChanged()
    {
        boolean changed = this.changed.get();
        this.changed.set(false);
        return changed;
    }
    
    @Override
    public void run()
    {
        try (final WatchService watchService = FileSystems.getDefault().newWatchService())
        {
            this.filePath.toAbsolutePath().getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!this.stop.get())
            {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                    {
                        Thread.yield();
                        continue;
                    }
                    else if (kind == StandardWatchEventKinds.ENTRY_MODIFY)
                    {
                        final Path changed = (Path) event.context();
                        if (changed.endsWith(this.filePath.getFileName().toString()))
                        {
                            this.changed.set(true);
                        }
                    }
                }
                if (!wk.reset()) break;
                Thread.yield();
            }
        }
        catch (Throwable e)
        {
            println(e);
        }
    }
}
