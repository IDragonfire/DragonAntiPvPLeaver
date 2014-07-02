package com.github.idragonfire.dapl_installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DAPL_Utils {

    public static boolean hasFile(JarFile jar, String name) {
        Enumeration<JarEntry> jarEntries = jar.entries();
        while (jarEntries.hasMoreElements()) {
            final JarEntry je = jarEntries.nextElement();
            if (je.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static String detectVersion(List<File> sourceFiles) {
        String version = null;
        for (File f : sourceFiles) {
            version = detectVersionFromOneFile(f);
            if (version != null) {
                return version;
            }
        }
        return null;
    }

    public static String detectVersionFromOneFile(File f) {
        String version = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = null;
            try {
                while ((line = in.readLine()) != null) {
                    version = analyseLine(line);
                    if (version != null) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String analyseLine(String line) {
        String version = null;
        String[] packages = new String[] { "net.minecraft.server.v",
                "org.bukkit.craftbukkit.v" };
        for (int i = 0; i < packages.length && version == null; i++) {
            version = analyseLine(line, packages[i]);
        }
        return version;
    }

    public static String analyseLine(String line, String nms) {
        String version = null;
        int start = line.indexOf(nms);
        if (start > 0) {
            try {
                int end = line.indexOf(".", start + nms.length() - 1);
                version = line.substring(start + nms.length() - 1, end);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return version;
    }

    public static String getNmsPath(String mcPackageVersion, String fileName) {
        return "net/minecraft/server/" + mcPackageVersion + "/" + fileName;
    }

    public static void deleteIfExists(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void copy(File from, File to) {
        try {
            Files.copy(from.toPath(), to.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean extractFromJar(JarFile jar, String fileName, File dest)
            throws IOException {
        File file = dest;
        if (file.isDirectory()) {
            // TODO: use logger
            // logger.log(Level.WARNING, "destination is a directory not a file");
            return false;
        }
        // if (!file.exists()) {
        // //TODO: file exists
        // file.getParentFile().mkdirs();
        // }

        Enumeration<JarEntry> jareEntries = jar.entries();
        while (jareEntries.hasMoreElements()) {
            JarEntry je = jareEntries.nextElement();
            if (!je.getName().equals(fileName)) {
                continue;
            }
            InputStream in = new BufferedInputStream(jar.getInputStream(je));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(
                    file));
            copyInputStream(in, out);
            return true;
        }
        return false;
    }

    public static String getMcPackageVersion(JarFile craftbukkitJar)
            throws IOException {
        String nmsPackagePath = "org/bukkit/craftbukkit/v";
        Enumeration<JarEntry> jareEntries = craftbukkitJar.entries();
        while (jareEntries.hasMoreElements()) {
            JarEntry je = jareEntries.nextElement();
            if (je.getName().startsWith(nmsPackagePath)) {
                String path = je.getName();
                int start = nmsPackagePath.length() - 1;
                int end = path.indexOf("/", start);
                return path.substring(start, end);
            }
        }
        return null;
    }

    public static void addFilesToExistingJar(File srcFile, File destFile,
            HashMap<String, File> files) throws IOException {

        byte[] buf = new byte[1024];

        ZipInputStream in = new ZipInputStream(new FileInputStream(srcFile));
        ZipOutputStream out = new ZipOutputStream(
                new FileOutputStream(destFile));

        ZipEntry entry = in.getNextEntry();
        while (entry != null) {
            if (!files.containsKey(entry.getName())) {
                // Add Jar entry to output stream.
                out.putNextEntry(new ZipEntry(entry.getName()));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = in.getNextEntry();
        }
        in.close();
        // Append new files
        for (String filename : files.keySet()) {
            InputStream in2 = new FileInputStream(files.get(filename));
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(filename));
            // Transfer bytes from the file to the JAR file
            int len;
            while ((len = in2.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in2.close();
        }
        out.close();
    }

    private final static void copyInputStream(final InputStream in,
            final OutputStream out) throws IOException {
        try {
            final byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) > 0) {
                out.write(buff, 0, n);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    public static void copy(File in, String out) {
        try {
            FileChannel inChannel = new FileInputStream(in).getChannel();
            FileChannel outChannel = new FileOutputStream(out).getChannel();
            try {
                int maxCount = (64 * 1024 * 1024) - (32 * 1024);
                long size = inChannel.size();
                long position = 0;
                while (position < size) {
                    position += inChannel.transferTo(position, maxCount,
                            outChannel);
                }
            } finally {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
