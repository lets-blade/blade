package com.hellokaton.blade.kit.reload;

import com.hellokaton.blade.Environment;
import com.hellokaton.blade.mvc.BladeConst;
import com.hellokaton.blade.options.StaticOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Eddie Wang on 12/25/17.
 */
@Slf4j
public class FileChangeDetector {
    private final WatchService watcher;
    private final Map<WatchKey, Path> pathMap = new HashMap<>();

    public FileChangeDetector(String dirPath) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        registerAll(Paths.get(dirPath));
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        pathMap.put(key, dir);
    }

    private void registerAll(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void processEvent(BiConsumer<WatchEvent.Kind<Path>, Path> processor) {
        for (; ; ) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                return;
            }

            Path dir = pathMap.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                Path filePath = dir.resolve(((WatchEvent<Path>) event).context());

                if (Files.isDirectory(filePath)) continue;
                log.info("File {} changes detected!", filePath.toString());
                //copy updated files to target
                processor.accept(kind, filePath);
            }
            key.reset();
        }
    }

    public static Path getDestPath(Path src, Environment env, StaticOptions staticOptions) {
        String templateDir = env.get(BladeConst.ENV_KEY_TEMPLATE_PATH, "/templates");
        List<String> templateOrStaticDirKeyword = new ArrayList<>();
        templateOrStaticDirKeyword.add(templateDir);
        templateOrStaticDirKeyword.addAll(staticOptions.getPaths());

        List<String> result = templateOrStaticDirKeyword.stream().filter(dir -> src.toString().contains(dir)).collect(Collectors.toList());
        if (result.size() != 1) {
            log.info("Cannot get dest dir");
            return null;
        }
        String key = result.get(0);
        log.info(BladeConst.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
        return Paths.get(BladeConst.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
    }
}