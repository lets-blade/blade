/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blade.kit.exception.IllegalPathException;

/**
 * 有关文件处理的工具类。
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 null ，而不会抛出 NullPointerException。
 * </p>
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class FileKit {

    private static final char COLON_CHAR = ':';
    private static final String UNC_PREFIX = "//";
    private static final String SLASH = "/";
    private static final char SLASH_CHAR = '/';
    private static final char BACKSLASH_CHAR = '\\';
    
    /** 当前目录记号："." */
    public static final String CURRENT_DIR = ".";

    /** 上级目录记号：".." */
    public static final String UP_LEVEL_DIR = "..";

    /**
     * 扩展名分隔符
     */
    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * UNIX文件路径分隔符
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * WINDOWS文件路径分隔符
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /** 临时文件前缀 */
    private static final String TEMP_FILE_PREFIX = "temp_file_prefix-";

    /**
     * 从URL中取得文件，如果URL为空，或不代表一个文件, 则返回null
     * 
     * @param url URL
     * 
     * @return 文件, 如果URL为空，或不代表一个文件, 则返回null
     */
    public static File toFile(URL url) {
        if (url == null) {
            return null;
        }

        if (!"file".equals(url.getProtocol())) {
            return null;
        }

        String path = url.getPath();

        return (path != null) ? new File(path) : null;

    }

    /**
     * 判断文件是否存在，如果path为null，则返回false
     * 
     * @param path 文件路径
     * @return 如果存在返回true
     */
    public static boolean exist(String path) {
        return (path == null) ? false : new File(path).exists();
    }
    
    /**
     * 判断文件是否存在，如果file为null，则返回false
     * 
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (file == null) ? false : file.exists();
    }

    /**
     * 是否存在匹配文件
     * 
     * @param directory 文件夹路径
     * @param regexp 文件夹中所包含文件名的正则表达式
     * @return 如果存在匹配文件返回true
     */
    public static boolean exist(String directory, String regexp) {
        File file = new File(directory);
        if (!file.exists()) {
            return false;
        }

        String[] fileList = file.list();
        if (fileList == null) {
            return false;
        }

        for (String fileName : fileList) {
            if (fileName.matches(regexp)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 判断是否为目录，如果path为null，则返回false
     * 
     * @param path 文件路径
     * @return 如果为目录true
     */
    public static boolean isDirectory(String path) {
        return (path == null) ? false : new File(path).isDirectory();
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     * 
     * @param file 文件
     * @return 如果为目录true
     */
    public static boolean isDirectory(File file) {
        return (file == null) ? false : file.isDirectory();
    }

    /**
     * 判断是否为文件，如果path为null，则返回false
     * 
     * @param path 文件路径
     * @return 如果为文件true
     */
    public static boolean isFile(String path) {
        return (path == null) ? false : new File(path).isDirectory();
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     * 
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return (file == null) ? false : file.isDirectory();
    }

    /**
     * 列出文件目录dir下以suffix结尾的子文件集合，非递归
     * <p>
     * 如果dir为null或不存在，则返回null
     * <p>
     * 如果dir不为目录，则返回null
     * <p>
     * 如果 suffix为null或""，则代表返回所有子文件
     * 
     * @param dir 文件目录
     * @param suffix 文件后缀
     * @return 目录dir下以suffix结尾的子文件集合，非递归
     */
    public static File[] listDirSuffixFiles(File dir, final String suffix) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists() || dir.isFile()) {
            return null;
        }

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return StringKit.isEmpty(suffix) ? true : (file.getName().endsWith(suffix));
            }
        });
    }

    /**
     * 列出文件目录dirPath下以suffix结尾的子文件集合，非递归
     * <p>
     * 如果dirPath为null或不存在，则返回null
     * <p>
     * 如果dirPath不为目录，则返回null
     * <p>
     * 如果 suffix为null或""，则代表返回所有子文件
     * 
     * @param dirPath 文件目录
     * @param suffix 文件后缀
     * @return 目录dirPath下以suffix结尾的子文件集合，非递归
     */
    public static File[] listDirSuffixFiles(String dirPath, final String suffix) {
        if (!exist(dirPath)) {
            return null;
        }
        File dir = new File(dirPath);

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return StringKit.isEmpty(suffix) ? true : (file.getName().endsWith(suffix));
            }
        });
    }

    /**
     * 列出文件目录dir下满足所有条件conditions的子文件集合，非递归
     * <p>
     * 如果dir为null或不存在，则返回null
     * <p>
     * 如果dir不为目录，则返回null
     * <p>
     * 如果conditions为null，则认为无条件限制
     * 
     * @param dir 文件目录
     * @param conditions 过滤条件
     * @return 目录dir下满足所有条件conditions的子文件集合，非递归
     */
    public static File[] listDirAllConditionFiles(File dir, final boolean...conditions) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists() || dir.isFile()) {
            return null;
        }

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (null == conditions || conditions.length == 0) {
                    return true;
                }
                for (boolean condition : conditions) {
                    if (!condition) {
                        return false;
                    }
                }

                return true;
            }
        });
    }

    /**
     * 列出文件目录dirPath下满足所有条件conditions的子文件集合，非递归
     * <p>
     * 如果dirPath为null或不存在，则返回null
     * <p>
     * 如果dirPath不为目录，则返回null
     * <p>
     * 如果conditions为null，则认为无条件限制
     * 
     * @param dirPath 文件目录
     * @param conditions 过滤条件
     * @return 目录dirPath下满足所有条件conditions的子文件集合，非递归
     */
    public static File[] listDirAllConditionFiles(String dirPath, final boolean...conditions) {
        if (!exist(dirPath)) {
            return null;
        }
        File dir = new File(dirPath);

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (null == conditions || conditions.length == 0) {
                    return true;
                }
                for (boolean condition : conditions) {
                    if (!condition) {
                        return false;
                    }
                }

                return true;
            }
        });
    }

    /**
     * 列出文件目录dir下满足任一条件conditions的子文件集合，非递归
     * <p>
     * 如果dir为null或不存在，则返回null
     * <p>
     * 如果dir不为目录，则返回null
     * <p>
     * 如果conditions为null，则认为无条件限制
     * 
     * @param dir 文件目录
     * @param conditions 过滤条件
     * @return 目录dir下满足任一条件conditions的子文件集合，非递归
     */
    public static File[] listDirAnyConditionFiles(File dir, final boolean...conditions) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists() || dir.isFile()) {
            return null;
        }

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (null == conditions || conditions.length == 0) {
                    return true;
                }
                for (boolean condition : conditions) {
                    if (condition) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    /**
     * 列出文件目录dirPath下满足任一条件conditions的子文件集合，非递归
     * <p>
     * 如果dirPath为null或不存在，则返回null
     * <p>
     * 如果dirPath不为目录，则返回null
     * <p>
     * 如果conditions为null，则认为无条件限制
     * 
     * @param dirPath 文件目录
     * @param conditions 过滤条件
     * @return 目录dirPath下满足任一条件conditions的子文件集合，非递归
     */
    public static File[] listDirAnyConditionFiles(String dirPath, final boolean...conditions) {
        if (!exist(dirPath)) {
            return null;
        }
        File dir = new File(dirPath);

        return dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (null == conditions || conditions.length == 0) {
                    return true;
                }
                for (boolean condition : conditions) {
                    if (condition) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    /**
     * 简单工厂
     * 
     * @param filename 文件名
     * @return new File(filename)
     */
    public static File file(String filename) {
        if (filename == null) {
            return null;
        }
        return new File(filename);
    }

    /**
     * 简单工厂
     * 
     * @param parent 父目录
     * @param child 子文件
     * @return new File(parent, child);
     */
    public static File file(File parent, String child) {
        if (child == null) {
            return null;
        }

        return new File(parent, child);
    }

    /**
     * 通过文件随机读取来加速
     * 
     * @param file 文件名
     * @return 文件内容
     * @throws IOException
     */
    public static byte[] readBytes(String file) throws IOException {
        return readBytes(file(file));
    }

    /**
     * 通过文件随机读取来加速
     * 
     * @param file 读取文件
     * @return 文件内容
     * @throws IOException
     */
    public static byte[] readBytes(File file) throws IOException {
        if (file == null || (!file.exists()) || !file.isFile()) {
            return null;
        }

        long length = file.length();
        if (length >= Integer.MAX_VALUE) {
            throw new RuntimeException("File is larger then max array size");
        }

        byte[] bytes = new byte[(int) length];
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.readFully(bytes);

            return bytes;
        } finally {
            StreamKit.close(randomAccessFile);
        }

    }

    /**
     * 获取文件大小（字节数）
     * 
     * @param fileName 文件路径
     * @return 文件大小（字节数），如果文件路径为空或者文件路径不存在 ,
     *         <p>
     *         则返回0
     */
    public static int getFileSize(String fileName) throws IOException {
        if (StringKit.isBlank(fileName)) {
            return 0;
        }

        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                return fis.available();
            }

            return 0;
        } finally {
            StreamKit.close(fis);
        }

    }

    /**
     * 创建文件，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     * @throws IOException
     */
    public static boolean createFile(String path) throws IOException {
        return createFile(path, false);
    }

    /**
     * 创建文件，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @param override 是否覆盖
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     * @throws IOException
     */
    public static boolean createFile(String path, boolean override) throws IOException {
        if (path == null) {
            return false;
        }

        File file = new File(path);
        if (file.exists() && !override) {
            return false;
        }

        if (file.isDirectory()) {
            return file.mkdirs();
        }

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        return file.createNewFile();
    }

    /**
     * 创建文件夹，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @param override 是否覆盖
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     */
    public static boolean createDir(String path, boolean override) {
        if (path == null) {
            return false;
        }

        File file = new File(path);
        if (file.exists() && !override) {
            return false;
        }

        return file.mkdirs();
    }

    /**
     * 创建文件夹，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     */
    public static boolean createDir(String path) {
        return createDir(path, false);
    }

    /**
     * 创建文件路径的父文件夹，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     */
    public static boolean createParentDir(String path) {
        return createParentDir(path, false);
    }

    public static boolean createParentDir(File file) {
        return createParentDir(file, false);
    }

    /**
     * 创建文件路径的父文件夹，不管文件层级，均可创建
     * 
     * @param path 文件路径
     * @param override 是否覆盖
     * @return 是否创建成功，如果path为空或者path为null ,则返回false
     */
    public static boolean createParentDir(String path, boolean override) {
        if (path == null) {
            return false;
        }

        return createDir(new File(path).getParent(), override);
    }

    public static boolean createParentDir(File file, boolean override) {
        if (file == null) {
            return false;
        }

        return createDir(file.getParent(), override);
    }

    /**
     * 刪除文件
     * 
     * @param file 文件
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }

        return file.delete();
    }

    /**
     * 刪除文件
     * 
     * @param path 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String path) {
        if (path == null) {
            return false;
        }

        return new File(path).delete();
    }

    /**
     * 删除文件及子文件
     * 
     * @param dir 文件夹
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * 删除文件及子文件
     * 
     * @param path 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDir(String path) {
        if (path == null) {
            return false;
        }

        return deleteDir(new File(path));
    }

    // FIXME
    public static Date lastModifiedTime(File file) {
        if (!exist(file)) {
            return null;
        }

        return new Date(file.lastModified());
    }

    public static Date lastModifiedTime(String path) {
        File file = new File(path);
        if (!exist(file)) {
            return null;
        }

        return new Date(file.lastModified());
    }

    // ==========================================================================
    // 规格化路径。
    // ==========================================================================

    /**
     * 规格化绝对路径。
     * <p>
     * 该方法返回以“/”开始的绝对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizeAbsolutePath(String path) throws IllegalPathException {
        return normalizePath(path, true, false, false);
    }

    /**
     * 规格化绝对路径。
     * <p>
     * 该方法返回以“/”开始的绝对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定removeTrailingSlash==true）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @param removeTrailingSlash 是否强制移除末尾的"/"
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    /**
     * 规格化路径。规则如下：
     * 
     * <ol>
     * <li>
     * 路径为null，则返回null。</li>
     * <li>
     * 将所有backslash("\\")转化成slash("/")。</li>
     * <li>
     * 去除重复的"/"或"\\"。</li>
     * <li>
     * 去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>
     * 空绝对路径返回"/"，空相对路径返回"./"。</li>
     * <li>
     * 保留路径末尾的"/"（如果有的话）。</li>
     * <li>
     * 对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，返回null。</li>
     * <li>
     * 对于Windows系统，有些路径有特殊的前缀，如驱动器名"c:"和UNC名"//hostname"，对于这些路径，保留其前缀， 并对其后的路径部分适用上述所有规则。</li>
     * <li>
     * Windows驱动器名被转换成大写，如"c:"转换成"C:"。</li>
     * </ol>
     * 
     * 
     * @param path 要规格化的路径
     * 
     * @return 规格化后的路径，如果路径非法，则返回null
     */
    public static String normalizeWindowsPath(String path) {
        return normalizePath(path, true);
    }

    /**
     * 规格化Unix风格的路径，不支持Windows驱动器名和UNC路径。
     * 
     * <p>
     * 转换规则如下：
     * 
     * <ol>
     * <li>
     * 路径为null，则返回null。</li>
     * <li>
     * 将所有backslash("\\")转化成slash("/")。</li>
     * <li>
     * 去除重复的"/"或"\\"。</li>
     * <li>
     * 去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>
     * 空绝对路径返回"/"，空相对路径返回"./"。</li>
     * <li>
     * 保留路径末尾的"/"（如果有的话）。</li>
     * <li>
     * 对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，返回null。</li>
     * </ol>
     * </p>
     * 
     * @param path 要规格化的路径
     * 
     * @return 规格化后的路径，如果路径非法，则返回null
     */
    public static String normalizeUnixPath(String path) {
        return normalizePath(path, false);
    }

    /**
     * 规格化相对路径。
     * <p>
     * 该方法返回不以“/”开始的相对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizeRelativePath(String path) throws IllegalPathException {
        return normalizePath(path, false, true, false);
    }

    /**
     * 规格化相对路径。
     * <p>
     * 该方法返回不以“/”开始的相对路径。转换规则如下：
     * </p>
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定removeTrailingSlash==true）。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @param removeTrailingSlash 是否强制移除末尾的"/"
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizeRelativePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, true, removeTrailingSlash);
    }

    /**
     * 规格化路径。规则如下：
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空绝对路径返回"/"，空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizePath(String path) throws IllegalPathException {
        return normalizePath(path, false, false, false);
    }

    /**
     * 规格化路径。规则如下：
     * <ol>
     * <li>路径为空，则返回""。</li>
     * <li>将所有backslash("\\")转化成slash("/")。</li>
     * <li>去除重复的"/"或"\\"。</li>
     * <li>去除"."，如果发现".."，则向上朔一级目录。</li>
     * <li>空绝对路径返回"/"，空相对路径返回""。</li>
     * <li>保留路径末尾的"/"（如果有的话，除了空路径和强制指定removeTrailingSlash==true）。</li>
     * <li>对于绝对路径，如果".."上朔的路径超过了根目录，则看作非法路径，抛出异常。</li>
     * </ol>
     * 
     * @param path 要规格化的路径
     * @param removeTrailingSlash 是否强制移除末尾的"/"
     * @return 规格化后的路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String normalizePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute, boolean forceRelative,
            boolean removeTrailingSlash) throws IllegalPathException {
        char[] pathChars = StringKit.trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // 检查绝对路径，以及path尾部的"/"
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == '/' || firstChar == '\\';
            endsWithSlash = lastChar == '/' || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append("/");
        }

        while (index < length) {
            // 跳到第一个非slash字符，或末尾
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            // 取得下一个slash index，或末尾
            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex - index);
            index = nextSlashIndex;

            // 忽略"."
            if (".".equals(element)) {
                continue;
            }

            // 回朔".."
            if ("..".equals(element)) {
                if (level == 0) {
                    // 如果是绝对路径，../试图越过最上层目录，这是不可能的，
                    // 抛出路径非法的异常。
                    if (isAbsolutePath) {
                        throw new IllegalPathException(path);
                    } else {
                        buf.append("../");
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            // 添加到path
            pathChars[level++] = (char) buf.length(); // 将已经读过的chars空间用于记录指定level的index
            buf.append(element).append('/');
        }

        // 除去最后的"/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == '/' || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != '/' && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }

    // ==========================================================================
    // 取得基于指定basedir规格化路径。
    // ==========================================================================

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * 
     * @param basedir 根目录，如果path为相对路径，表示基于此目录
     * @param path 要检查的路径
     * @return 规格化的绝对路径
     * @throws IllegalPathException 如果路径非法
     */
    public static String getAbsolutePathBasedOn(String basedir, String path) throws IllegalPathException {
        // 如果path为绝对路径，则规格化后返回
        boolean isAbsolutePath = false;

        path = StringKit.trimToEmpty(path);

        if (path.length() > 0) {
            char firstChar = path.charAt(0);
            isAbsolutePath = firstChar == '/' || firstChar == '\\';
        }

        if (!isAbsolutePath) {
            // 如果path为相对路径，将它和basedir合并。
            if (path.length() > 0) {
                path = StringKit.trimToEmpty(basedir) + "/" + path;
            } else {
                path = StringKit.trimToEmpty(basedir);
            }
        }

        return normalizeAbsolutePath(path);
    }

    /**
     * 取得和系统相关的绝对路径。
     * 
     * @throws IllegalPathException 如果basedir不是绝对路径
     */
    public static String getSystemDependentAbsolutePathBasedOn(String basedir, String path) {
        path = StringKit.trimToEmpty(path);

        boolean endsWithSlash = path.endsWith("/") || path.endsWith("\\");

        File pathFile = new File(path);

        if (pathFile.isAbsolute()) {
            // 如果path已经是绝对路径了，则直接返回之。
            path = pathFile.getAbsolutePath();
        } else {
            // 否则以basedir为基本路径。
            // 下面确保basedir本身为绝对路径。
            basedir = StringKit.trimToEmpty(basedir);

            File baseFile = new File(basedir);

            if (baseFile.isAbsolute()) {
                path = new File(baseFile, path).getAbsolutePath();
            } else {
                throw new IllegalPathException("Basedir is not absolute path: " + basedir);
            }
        }

        if (endsWithSlash) {
            path = path + '/';
        }

        return normalizePath(path);
    }

    /**
     * 取得和系统相关的文件名前缀。对于Windows系统，可能是驱动器名或UNC路径前缀"//hostname"。如果不存在前缀，则返回空字符串。
     * 
     * @param path 绝对路径
     * @param isWindows 是否为windows系统
     * 
     * @return 和系统相关的文件名前缀，如果路径非法，例如："//"，则返回null
     */
    private static String getSystemDependentPrefix(String path, boolean isWindows) {
        if (isWindows) {
            // 判断UNC路径
            if (path.startsWith(UNC_PREFIX)) {
                // 非法UNC路径："//"
                if (path.length() == UNC_PREFIX.length()) {
                    return null;
                }

                // 假设路径为//hostname/subpath，返回//hostname
                int index = path.indexOf(SLASH, UNC_PREFIX.length());

                if (index != -1) {
                    return path.substring(0, index);
                } else {
                    return path;
                }
            }

            // 判断Windows绝对路径："c:/..."
            if ((path.length() > 1) && (path.charAt(1) == COLON_CHAR)) {
                return path.substring(0, 2).toUpperCase();
            }
        }

        return "";
    }

    // ==========================================================================
    // 取得相对于指定basedir相对路径。
    // ==========================================================================

    /**
     * 取得相对于指定根目录的相对路径。
     * 
     * @param basedir 根目录
     * @param path 要计算的路径
     * @return 如果path和basedir是兼容的，则返回相对于 basedir的相对路径，否则返回path本身。
     * @throws IllegalPathException 如果路径非法
     */
    public static String getRelativePath(String basedir, String path) throws IllegalPathException {
        // 取得规格化的basedir，确保其为绝对路径
        basedir = normalizeAbsolutePath(basedir);

        // 取得规格化的path
        path = getAbsolutePathBasedOn(basedir, path);

        // 保留path尾部的"/"
        boolean endsWithSlash = path.endsWith("/");

        // 按"/"分隔basedir和path
        String[] baseParts = StringKit.split(basedir, '/');
        String[] parts = StringKit.split(path, '/');
        StringBuilder buf = new StringBuilder();
        int i = 0;

        while (i < baseParts.length && i < parts.length && baseParts[i].equals(parts[i])) {
            i++;
        }

        if (i < baseParts.length && i < parts.length) {
            for (int j = i; j < baseParts.length; j++) {
                buf.append("..").append('/');
            }
        }

        for (; i < parts.length; i++) {
            buf.append(parts[i]);

            if (i < parts.length - 1) {
                buf.append('/');
            }
        }

        if (endsWithSlash && buf.length() > 0 && buf.charAt(buf.length() - 1) != '/') {
            buf.append('/');
        }

        return buf.toString();
    }

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * 
     * <p>
     * 该方法自动判定操作系统的类型，如果是windows系统，则支持UNC路径和驱动器名。
     * </p>
     * 
     * @param basedir 根目录，如果path为相对路径，表示基于此目录
     * @param path 要检查的路径
     * 
     * @return 规格化的路径，如果path非法，或basedir为 null，则返回null
     */
    public static String getPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, SystemKit.getOsInfo().isWindows());
    }

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * 
     * @param basedir 根目录，如果path为相对路径，表示基于此目录
     * @param path 要检查的路径
     * 
     * @return 规格化的路径，如果path非法，或basedir为 null，则返回null
     */
    public static String getWindowsPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, true);
    }

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * 
     * @param basedir 根目录，如果path为相对路径，表示基于此目录
     * @param path 要检查的路径
     * 
     * @return 规格化的路径，如果path非法，或basedir为 null，则返回null
     */
    public static String getUnixPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, false);
    }

    /**
     * 如果指定路径已经是绝对路径，则规格化后直接返回之，否则取得基于指定basedir的规格化路径。
     * 
     * @param basedir 根目录，如果path为相对路径，表示基于此目录
     * @param path 要检查的路径
     * @param isWindows 是否是windows路径，如果为true，则支持驱动器名和UNC路径
     * 
     * @return 规格化的路径，如果path非法，或basedir为 null，则返回null
     */
    private static String getPathBasedOn(String basedir, String path, boolean isWindows) {
        /*
         * ------------------------------------------- * 首先取得path的前缀，判断是否为绝对路径。 * 如果已经是绝对路径，则调用normalize后返回。 *
         * -------------------------------------------
         */
        if (path == null) {
            return null;
        }

        path = path.trim();

        // 将"\\"转换成"/"，以便统一处理
        path = path.replace(BACKSLASH_CHAR, SLASH_CHAR);

        // 取得系统特定的路径前缀，对于windows系统，可能是："C:"或是"//hostname"
        String prefix = getSystemDependentPrefix(path, isWindows);

        if (prefix == null) {
            return null;
        }

        // 如果是绝对路径，则直接返回
        if ((prefix.length() > 0)
                || ((path.length() > prefix.length()) && (path.charAt(prefix.length()) == SLASH_CHAR))) {
            return normalizePath(path, isWindows);
        }

        /*
         * ------------------------------------------- * 现在已经确定path是相对路径了，因此我们要 * 将它和basedir合并。 *
         * -------------------------------------------
         */
        if (basedir == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(basedir.trim());

        // 防止重复的"/"，否则容易和UNC prefix混淆
        if ((basedir.length() > 0) && (path.length() > 0) && (basedir.charAt(basedir.length() - 1) != SLASH_CHAR)) {
            builder.append(SLASH_CHAR);
        }

        builder.append(path);

        return normalizePath(builder.toString(), isWindows);
    }

    /**
     * 取得相对于指定根目录的相对路径。
     * 
     * @param basedir 根目录
     * @param path 要计算的路径
     * 
     * @return 如果path和basedir是兼容的，则返回相对于 basedir的相对路径，否则返回path本身。如果
     *         basedir不是绝对路径，或者路径非法，则返回null
     */
    public static String getWindowsRelativePath(String basedir, String path) {
        return getRelativePath(basedir, path, true);
    }

    /**
     * 取得相对于指定根目录的相对路径。
     * 
     * @param basedir 根目录
     * @param path 要计算的路径
     * 
     * @return 如果path和basedir是兼容的，则返回相对于 basedir的相对路径，否则返回path本身。如果
     *         basedir不是绝对路径，或者路径非法，则返回null
     */
    public static String getUnixRelativePath(String basedir, String path) {
        return getRelativePath(basedir, path, false);
    }

    /**
     * 取得相对于指定根目录的相对路径。
     * 
     * @param basedir 根目录
     * @param path 要计算的路径
     * @param isWindows 是否是windows路径，如果为true，则支持驱动器名和UNC路径
     * 
     * @return 如果path和basedir是兼容的，则返回相对于 basedir的相对路径，否则返回path本身。如果
     *         basedir不是绝对路径，或者路径非法，则返回null
     */
    private static String getRelativePath(String basedir, String path, boolean isWindows) {
        // 取得规格化的basedir，确保其为绝对路径
        basedir = normalizePath(basedir, isWindows);

        if (basedir == null) {
            return null;
        }

        String basePrefix = getSystemDependentPrefix(basedir, isWindows);

        if ((basePrefix == null) || ((basePrefix.length() == 0) && !basedir.startsWith(SLASH))) {
            return null; // basedir必须是绝对路径
        }

        // 取得规格化的path
        path = getPathBasedOn(basedir, path, isWindows);

        if (path == null) {
            return null;
        }

        String prefix = getSystemDependentPrefix(path, isWindows);

        // 如果path和basedir的前缀不同，则不能转换成相对于basedir的相对路径。
        // 直接返回规格化的path即可。
        if (!basePrefix.equals(prefix)) {
            return path;
        }

        // 保留path尾部的"/"
        boolean endsWithSlash = path.endsWith(SLASH);

        // 按"/"分隔basedir和path
        String[] baseParts = StringKit.split(basedir.substring(basePrefix.length()), SLASH_CHAR);
        String[] parts = StringKit.split(path.substring(prefix.length()), SLASH_CHAR);
        StringBuilder builder = new StringBuilder();
        int i = 0;

        if (isWindows) {
            while ((i < baseParts.length) && (i < parts.length) && baseParts[i].equalsIgnoreCase(parts[i])) {
                i++;
            }
        } else {
            while ((i < baseParts.length) && (i < parts.length) && baseParts[i].equals(parts[i])) {
                i++;
            }
        }

        if ((i < baseParts.length) && (i < parts.length)) {
            for (int j = i; j < baseParts.length; j++) {
                builder.append(UP_LEVEL_DIR).append(SLASH_CHAR);
            }
        }

        for (; i < parts.length; i++) {
            builder.append(parts[i]);

            if (i < (parts.length - 1)) {
                builder.append(SLASH_CHAR);
            }
        }

        if (builder.length() == 0) {
            builder.append(CURRENT_DIR);
        }

        String relpath = builder.toString();

        if (endsWithSlash && !relpath.endsWith(SLASH)) {
            relpath += SLASH;
        }

        return relpath;
    }

    // ==========================================================================
    // 取得文件名后缀。
    // ==========================================================================

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回null。</li>
     * <li>文件名没有后缀 - 返回null。</li>
     * </ul>
     */
    public static String getExtension(String fileName) {
        return getExtension(fileName, null, false);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回null。</li>
     * <li>文件名没有后缀 - 返回null。</li>
     * </ul>
     */
    public static String getExtension(String fileName, boolean toLowerCase) {
        return getExtension(fileName, null, toLowerCase);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回null。</li>
     * <li>文件名没有后缀 - 返回指定字符串nullExt。</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt) {
        return getExtension(fileName, nullExt, false);
    }

    /**
     * 取得文件路径的后缀。
     * <ul>
     * <li>未指定文件名 - 返回null。</li>
     * <li>文件名没有后缀 - 返回指定字符串nullExt。</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt, boolean toLowerCase) {
        fileName = StringKit.trimToNull(fileName);

        if (fileName == null) {
            return null;
        }

        fileName = fileName.replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        int index = fileName.lastIndexOf(".");
        String ext = null;

        if (index >= 0) {
            ext = StringKit.trimToNull(fileName.substring(index + 1));
        }

        if (ext == null) {
            return nullExt;
        }

        return toLowerCase ? ext.toLowerCase() : ext;
    }


    /**
     * 规格化文件名后缀。
     * <ul>
     * <li>除去两边空白。</li>
     * <li>转成小写。</li>
     * <li>除去开头的“.”。</li>
     * <li>对空白的后缀，返回null。</li>
     * </ul>
     */
    public static String normalizeExtension(String ext) {
        ext = StringKit.trimToNull(ext);

        if (ext != null) {
            ext = ext.toLowerCase();

            if (ext.startsWith(".")) {
                ext = StringKit.trimToNull(ext.substring(1));
            }
        }

        return ext;
    }

    // FIXME
    public static class FileNameAndExtension {
        private final String fileName;
        private final String extension;

        private FileNameAndExtension(String fileName, String extension, boolean extensionToLowerCase) {
            this.fileName = fileName;
            this.extension = extensionToLowerCase ? StringKit.toLowerCase(extension) : extension;
        }

        public String getFileName() {
            return fileName;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return extension == null ? fileName : fileName + "." + extension;
        }
    }

    // ==========================================================================
    // 文件名相关处理。
    // ==========================================================================

    /**
     * 获取文件名，即文件全名去掉路径,结果与平台无关，保持统一。
     * <p>
     * 如果文件名filename为null，则返回null
     * <p>
     * 如果文件名不存在，则返回""
     * 
     * <pre>
     * {@code
     * a/b/c.txt --> c.txt
     * a.txt     --> a.txt
     * a/b/c     --> c
     * a/b/c/    --> ""
     * }
     * </pre>
     * <p>
     * 
     * 
     * @param filename 文件名
     * @return 去除路径后的文件名
     */
    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    /**
     * 返回最后一个文件分隔符的索引位，如果文件为null，则返回-1
     * 
     * @param filename 文件名
     * @return 最后一个文件分隔符的索引位
     */
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }

        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * 获取扩展名的索引位，即最后一个"."
     * <p>
     * 如果文件为null，则返回-1
     * 
     * @param filename 文件名
     * @return 扩展名的索引位
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }

    // ==========================================================================
    // 文件资源。
    // ==========================================================================

    private static final Pattern schemePrefixPattern = Pattern.compile(
            "(file:/*[a-z]:)|(\\w+://.+?/)|((jar|zip):.+!/)|(\\w+:)", Pattern.CASE_INSENSITIVE);

    /**
     * 根据指定url和相对路径，计算出相对路径所对应的完整url。类似于URI.resolve() 方法，然后后者不能正确处理jar类型的URL。
     */
    public static String resolve(String url, String relativePath) {
        url = StringKit.trimToEmpty(url);

        Matcher m = schemePrefixPattern.matcher(url);
        int index = 0;

        if (m.find()) {
            index = m.end();

            if (url.charAt(index - 1) == '/') {
                index--;
            }
        }

        return url.substring(0, index) + normalizeAbsolutePath(url.substring(index) + "/../" + relativePath);
    }

    /**
     * 获取资源名对应的文件对象
     * 
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @return 文件, 如果资源找不到, 则返回null
     */
    public static File getResourcesFile(String resourceName) {
        URL url = FileKit.class.getResource(resourceName);
        if (url == null) {
            return null;
        }

        String filePath = url.getFile();
        return new File(filePath);
    }

    /**
     * 获取url对应的文件对象
     * 
     * @param url
     * @see URL
     * @return 文件，如果url为null,则返回null
     */
    public static File getResourcesFile(URL url) {
        if (url == null) {
            return null;
        }

        String filePath = url.getFile();
        return new File(filePath);
    }

    public static File createAndReturnFile(String filename) throws IOException {
        File file = newFile(filename);
        if (file != null && !file.canWrite()) {
            String dirName = file.getPath();
            int i = dirName.lastIndexOf(File.separator);
            if (i > -1) {
                dirName = dirName.substring(0, i);
                File dir = newFile(dirName);
                dir.mkdirs();
            }

            file.createNewFile();
        }
        return file;
    }

    public static File newFile(String pathName) throws IOException {
        if (StringKit.isBlank(pathName)) {
            return null;
        }

        return new File(pathName).getCanonicalFile();
    }

    // ==========================================================================
    // 创建临时文件。
    // ==========================================================================

    public static File createTempDirectory(String prefix, String suffix) throws IOException {
        return createTempDirectory(prefix, suffix, (File) null);
    }

    public static File createTempDirectory(String prefix, String suffix, String tempDirName) throws IOException {
        return createTempDirectory(prefix, suffix, new File(tempDirName));
    }

    public static File createTempDirectory(String prefix, String suffix, File tempDir) throws IOException {
        File file = doCreateTempFile(prefix, suffix, tempDir);
        file.delete();
        file.mkdir();
        return file;
    }

    public static File createTempFile() throws IOException {
        return createTempFile(true);
    }

    public static File createTempFile(boolean create) throws IOException {
        return createTempFile(TEMP_FILE_PREFIX, ".tmp", (File) null, create);
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, (File) null, true);
    }

    public static File createTempFile(String prefix, String suffix, boolean create) throws IOException {
        return createTempFile(prefix, suffix, (File) null, create);
    }

    public static File createTempFile(String prefix, String suffix, String tempDirName) throws IOException {
        return createTempFile(prefix, suffix, new File(tempDirName), true);
    }

    public static File createTempFile(String prefix, String suffix, File tempDir) throws IOException {
        return createTempFile(prefix, suffix, tempDir, true);
    }

    public static File createTempFile(String prefix, String suffix, String tempDirName, boolean create)
            throws IOException {
        return createTempFile(prefix, suffix, new File(tempDirName), create);
    }

    public static File createTempFile(String prefix, String suffix, File tempDir, boolean create) throws IOException {
        // FIXME
        if (StringKit.isBlank(prefix)) {
            return null;
        }

        File file = doCreateTempFile(prefix, suffix, tempDir);
        file.delete();
        if (create) {
            file.createNewFile();
        }
        return file;
    }

    private static File doCreateTempFile(String prefix, String suffix, File dir) throws IOException {
        int exceptionsCount = 0;
        while (true) {
            try {
                return File.createTempFile(prefix, suffix, dir).getCanonicalFile();
            } catch (IOException e) { // fixes

                if (++exceptionsCount >= 100) {
                    throw e;
                }
            }
        }
    }

    // ==========================================================================

    public static String getClassFilePath(Class<?> clazz) throws IOException {
        if (clazz == null) {
            return null;
        }

        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw ExceptionKit.toRuntimeException(e);
        }
        File file = new File(filePath);
        return file.getAbsolutePath();
    }
    
    /**
	 * 复制单个文件
	 * @param sourceFile  准备复制的文件源
	 * @param destFile 拷贝到新绝对路径带文件名
	 * @return
     * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public static void copy(String sourceFile, String destFile) throws IOException {
			
		File source = new File(sourceFile);
		if (source.exists()) {
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				File dest = new File(destFile);
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} finally {
				inputChannel.close();
				outputChannel.close();
			}
		}
	}

	/**
	 * 复制整个文件夹的内容
	 * @param oldPath  准备拷贝的目录
	 * @param newPath  指定绝对路径的新目录
	 * @return
	 */
	@SuppressWarnings("resource")
	public static void copyDir(String oldPath, String newPath) {
		try {
			/**如果文件夹不存在 则建立新文件**/
			new File(newPath).mkdirs(); 
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					
					FileChannel inputChannel = null;
					FileChannel outputChannel = null;
					try {
						File dest = new File(newPath + "/" + (temp.getName()).toString());
						inputChannel = new FileInputStream(temp).getChannel();
						outputChannel = new FileOutputStream(dest).getChannel();
						outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
					} finally {
						inputChannel.close();
						outputChannel.close();
					}
				}
				/**如果是子文件**/
				if (temp.isDirectory()) {
					copyDir(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件
	 * @param oldPath
	 * @param newPath
	 * @return
	 * @throws IOException 
	 */
	public static void moveFile(String oldPath, String newPath) throws IOException {
		copy(oldPath, newPath);
		delete(oldPath);
	}

	/**
	 * 移动目录
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyDir(oldPath, newPath);
		deleteDir(oldPath);
	}
	
}