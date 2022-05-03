package com.hellokaton.blade.watcher;

import com.hellokaton.blade.Environment;
import com.hellokaton.blade.event.Event;
import com.hellokaton.blade.event.EventType;
import com.hellokaton.blade.mvc.BladeConst;
import com.hellokaton.blade.mvc.WebContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

/**
 * Environment watcher
 *
 * @author biezhi
 * @date 2017/12/24
 */
@Slf4j
public class EnvironmentWatcher implements Runnable {

    @Override
    public void run() {
        if (BladeConst.CLASSPATH.endsWith(".jar")) {
            return;
        }

        final Path path = Paths.get(BladeConst.CLASSPATH);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            // start an infinite loop
            while (true) {
                final WatchKey key = watchService.take();
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    // get the filename for the event
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    final String           filename       = watchEventPath.context().toString();
                    // print it out
                    if (log.isDebugEnabled()) {
                        log.debug("⬢ {} -> {}", kind, filename);
                    }
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE &&
                            filename.startsWith(".app") && filename.endsWith(".properties.swp")) {
                        // reload env
                        log.info("⬢ Reload environment");

                        Environment environment = Environment.of("classpath:" + filename.substring(1, filename.length() - 4));
                        WebContext.blade().environment(environment);
                        // notify
                        WebContext.blade().eventManager().fireEvent(EventType.ENVIRONMENT_CHANGED, new Event().attribute("environment", environment));
                    }
                }
                // reset the keyf
                boolean valid = key.reset();
                // exit loop if the key is not valid (if the directory was
                // deleted, for
                if (!valid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            log.error("Environment watch error", ex);
        }
    }

}
