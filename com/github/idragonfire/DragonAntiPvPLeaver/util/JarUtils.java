package com.github.idragonfire.DragonAntiPvPLeaver.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.github.idragonfire.DragonAntiPvPLeaver.DAPL_Plugin;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

public class JarUtils {
    protected boolean runing_from_jar = false;
    protected URL ressource;
    protected Logger logger;
    protected String mc_version;

    public JarUtils(Logger logger) {
        mc_version = "v1_5_R2";
        this.logger = logger;
        ressource = JarUtils.class.getClassLoader().getResource("plugin.yml");
        runing_from_jar = ressource != null;
    }

    public JarFile getRunningJar() throws IOException {
        if (!runing_from_jar) {
            logger.log(Level.WARNING, "Plugin is not running?");
            return null; // null if not running from jar
        }
        String path = new File(JavaUtils.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();
        path = URLDecoder.decode(path, "UTF-8");
        return new JarFile(path);
    }

    public boolean hasFile(JarFile jar, String name) {
        Enumeration<JarEntry> jarEntries = jar.entries();
        while (jarEntries.hasMoreElements()) {
            final JarEntry je = jarEntries.nextElement();
            if (je.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getMcPackageVersion() {
        return mc_version;
    }

    public String getNmsPath(String fileName) {
        return "net/minecraft/server/" + getMcPackageVersion() + "/" + fileName;
    }

    public boolean extractFromJar(JarFile jar, String fileName, File dest)
            throws IOException {
        File file = dest;
        if (file.isDirectory()) {
            logger.log(Level.WARNING, "destination is a directory not a file");
            return false;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

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

    public File getCraftBukkitLocation() {
        try {
            return new File(Bukkit.getServer().getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getPluginJar(DAPL_Plugin plugin) {
        return plugin.getFile();
    }

    public File getTmpDir(Plugin plugin) {
        // File pluginFolder = plugin.getDataFolder();
        // if (!pluginFolder.exists()) {
        // pluginFolder.mkdir();
        // }
        File tmpFolder = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separatorChar + getMcPackageVersion());
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
        return tmpFolder;
    }

    public static void main(String[] args) {
        JarUtils u = new JarUtils(null);
        try {
            JarFile bukkit = new JarFile(
                    "D:\\Games\\Minecraft\\1.5.1\\craftbukkit.jar");
            System.out.println(""
                    + u.hasFile(bukkit,
                            "net/minecraft/server/DAPL_Injection.class"));
            u.extractFromJar(bukkit, u.getNmsPath("PlayerConnection.class"),
                    new File("C:\\dada\\newnewnew.class"));
            // copy("D:\\Games\\Minecraft\\1.5.1\\craftbukkit.jar",
            // "D:\\Games\\Minecraft\\1.5.1\\craftbukkit.jar.new");
            HashMap<String, File> files = new HashMap<String, File>();
            files
                    .put(
                            "net/minecraft/server/DAPL_Injection.class",
                            new File(
                                    "D:\\workspace\\DragonAntiPvpLeaver\\bin\\net\\minecraft\\server\\DAPL_Injection.class"));
            files.put("net/minecraft/server/" + u.mc_version
                    + "/PlayerConnection.class", new File(
                    "C:\\dada\\PlayerConnection.class"));

            addFilesToExistingJar(new File(
                    "D:\\Games\\Minecraft\\1.5.1\\craftbukkit.jar"), new File(
                    "D:\\Games\\Minecraft\\1.5.1\\craftbukkit.jar.new"), files);
            System.out.println("finished"
                    + u.getClass().getProtectionDomain().getCodeSource());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void copy(File in, String out) {
        try {
            FileChannel inChannel = new FileInputStream(in).getChannel();
            FileChannel outChannel = new FileOutputStream(out).getChannel();
            try {
                // inChannel.transferTo(0, inChannel.size(), outChannel); // original -- apparently has trouble copying large files on Windows
                // magic number for Windows, (64Mb - 32Kb)
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
