package com.blade.ioc.reader;

import com.blade.ioc.Scanner;
import com.blade.kit.BladeKit;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract Class Reader
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
@Slf4j
public abstract class AbstractClassReader implements ClassReader {

    @Override
    public Set<ClassInfo> readClasses(Scanner scanner) {
        return this.getClassByAnnotation(scanner.getPackageName(), scanner.getParent(), scanner.getAnnotation(), scanner.isRecursive());
    }

    /**
     * Get class by condition
     *
     * @param packageName
     * @param packagePath
     * @param parent
     * @param annotation
     * @param recursive
     * @return
     */
    private Set<ClassInfo> findClassByPackage(final String packageName, final String packagePath,
                                              final Class<?> parent, final Class<? extends Annotation> annotation,
                                              final boolean recursive, Set<ClassInfo> classes) throws ClassNotFoundException {

        // Get the directory of this package to create a File
        File dir = new File(packagePath);
        // If not exist or is not a direct return to the directory
        if ((!dir.exists()) || (!dir.isDirectory())) {
            log.warn("The package [{}] not found.", packageName);
        }
        // If present, get all the files under the package include the directory
        File[] dirFiles = accept(dir, recursive);
        // Loop all files
        if (null != dirFiles && dirFiles.length > 0) {
            for (File file : dirFiles) {
                // If it is a directory, continue scanning
                if (file.isDirectory()) {
                    findClassByPackage(packageName + '.' + file.getName(), file.getAbsolutePath(), parent, annotation, recursive, classes);
                } else {
                    // If the java class file is removed later. Class only leave the class name
                    String   className = file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz     = Class.forName(packageName + '.' + className);
                    if (null != parent && null != annotation) {
                        if (null != clazz.getSuperclass() && clazz.getSuperclass().equals(parent) &&
                                null != clazz.getAnnotation(annotation)) {
                            classes.add(new ClassInfo(clazz));
                        }
                        continue;
                    }
                    if (null != parent) {
                        if (null != clazz.getSuperclass() && clazz.getSuperclass().equals(parent)) {
                            classes.add(new ClassInfo(clazz));
                        } else {
                            if (null != clazz.getInterfaces() && clazz.getInterfaces().length > 0 && clazz.getInterfaces()[0].equals(parent)) {
                                classes.add(new ClassInfo(clazz));
                            }
                        }
                        continue;
                    }
                    if (null != annotation) {
                        if (null != clazz.getAnnotation(annotation)) {
                            classes.add(new ClassInfo(clazz));
                        }
                        continue;
                    }
                    classes.add(new ClassInfo(clazz));
                }
            }
        }
        return classes;
    }

    /**
     * Filter the file rules
     *
     * @param file
     * @param recursive
     * @return
     */
    private File[] accept(File file, final boolean recursive) {
        // Custom filtering rules If you can loop (include subdirectories) or is the end of the file. Class (compiled java class file)
        return file.listFiles(file1 -> (recursive && file1.isDirectory()) || (file1.getName().endsWith(".class")));
    }

    public Set<ClassInfo> getClassByAnnotation(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
        Set<ClassInfo> classes = new HashSet<>();
        // Get the name of the package and replace it
        String packageDirName = packageName.replace('.', '/');
        // Defines an enumerated collection and loops to process the URL in this directory
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            // Loop iterations down
            while (dirs.hasMoreElements()) {
                URL            url        = dirs.nextElement();
                String         filePath   = new URI(url.getFile()).getPath();
                Set<ClassInfo> subClasses = findClassByPackage(packageName, filePath, parent, annotation, recursive, classes);
                if (BladeKit.isNotEmpty(subClasses)) {
                    classes.addAll(subClasses);
                }
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            log.error("Add user custom view class error Can't find such Class files.");
        }
        return classes;
    }

}