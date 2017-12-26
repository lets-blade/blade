package com.blade.kit.reload;

import com.blade.Environment;
import com.blade.mvc.Const;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Eddie on 12/25/17.
 */
@Slf4j
public class FileChangeDetector {
    WatchService watcher;
    Map<WatchKey, Path> pathMap = new HashMap<>();

    public FileChangeDetector(String dirPath) throws IOException{
        watcher = FileSystems.getDefault().newWatchService();
        registerAll(Paths.get(dirPath));
    }

    private void register(Path dir) throws IOException{
        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
        pathMap.put(key,dir);
    }

    private void registerAll(Path dir) throws  IOException{
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void processEvent(BiConsumer<WatchEvent.Kind<Path>, Path> processor){
        for(;;){
            WatchKey key;
            try{
                key = watcher.take();
            }catch (InterruptedException e) {
                return;
            }

            log.info("File changes detected!");
            Path dir = pathMap.get(key);

            for (WatchEvent<?> event: key.pollEvents()){
                WatchEvent.Kind kind = event.kind();
                Path filePath = dir.resolve(((WatchEvent<Path>)event).context());

                if(Files.isDirectory(filePath)) continue;
                //copy updated files to target
                processor.accept(kind, filePath);
            }
            key.reset();
        }
    }

    public static Path generateDestPath(Path src, Environment env){
        String tempalteDir = env.get(Const.ENV_KEY_TEMPLATE_PATH,"/templates");
        Optional<String> staticDir =  env.get(Const.ENV_KEY_STATIC_DIRS);
        List<String> templateOrStaticDirKeyword = new ArrayList<>();
        templateOrStaticDirKeyword.add(tempalteDir);
        if(staticDir.isPresent()){
            templateOrStaticDirKeyword.addAll(Arrays.asList(staticDir.get().split(",")));
        }else{
            templateOrStaticDirKeyword.addAll(Const.DEFAULT_STATICS);
        }

        List result = templateOrStaticDirKeyword.stream().filter(dir -> src.toString().indexOf(dir)!=-1).collect(Collectors.toList());
        if(result.size()!=1){
            log.info("Cannot analyse the dest dir");
            return  null;
        }
        String key = (String)result.get(0);
        log.info(Const.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
        return Paths.get(Const.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
    }
}
