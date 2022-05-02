package com.blade.kit.reload;

import com.blade.Environment;
import com.blade.mvc.Const;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Eddie Wang on 12/25/17.
 */
@Slf4j
public class FileChangeDetector {
    private WatchService watcher;
    private Map<WatchKey, Path> pathMap = new HashMap<>();

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

            Path dir = pathMap.get(key);
            for (WatchEvent<?> event: key.pollEvents()){
                WatchEvent.Kind kind = event.kind();
                Path filePath = dir.resolve(((WatchEvent<Path>)event).context());

                if(Files.isDirectory(filePath)) continue;
                log.info("File {} changes detected!",filePath.toString());
                //copy updated files to target
                processor.accept(kind, filePath);
            }
            key.reset();
        }
    }

    public static Path getDestPath(Path src, Environment env){
        String templateDir = env.get(Const.ENV_KEY_TEMPLATE_PATH,"/templates");
        Optional<String> staticDir =  env.get(Const.ENV_KEY_STATIC_DIRS);
        List<String> templateOrStaticDirKeyword = new ArrayList<>();
        templateOrStaticDirKeyword.add(templateDir);
        if(staticDir.isPresent()){
            templateOrStaticDirKeyword.addAll(Arrays.asList(staticDir.get().split(",")));
        }else{
            templateOrStaticDirKeyword.addAll(Const.DEFAULT_STATICS);
        }

        List result = templateOrStaticDirKeyword.stream().filter(dir -> src.toString().indexOf(dir)!=-1).collect(Collectors.toList());
        if(result.size()!=1){
            log.info("Cannot get dest dir");
            return  null;
        }
        String key = (String)result.get(0);
        log.info(Const.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
        return Paths.get(Const.CLASSPATH + src.toString().substring(src.toString().indexOf(key)));
    }
}