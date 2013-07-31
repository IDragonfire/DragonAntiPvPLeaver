package com.github.idragonfire.dapl_installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class DAPL_Transformer {
    public static final String FIELD_DELAYED = "dragonfire_dapl_delay_disconnect";
    public static final String FIELD_CONTINUE = "dragonfire_dapl_continue_disconnect";
    public static String LINE_SEPERATOR = System.getProperty("line.separator");

    private String mcPackageVersion;
    private String playerConnectionDisconnectMethodName;
    private String entityBaseTickMethod;

    public byte[] transform(byte[] data) {
        try {
            CtClass cc = ClassPool.getDefault().makeClass(
                    new java.io.ByteArrayInputStream(data));
            cc.addField(CtField.make("public boolean " + FIELD_DELAYED
                    + " = false;", cc));
            cc.addField(CtField.make("public boolean " + FIELD_CONTINUE
                    + " = false;", cc));
            CtMethod disconnectMethod = cc.getDeclaredMethod(
                    getPlayerConnectionDisconnectMethodName(),
                    findPlayerConnectionDisconnectMethodParams());
            disconnectMethod.insertBefore(
            // check if plugin allow disconnect
                    "{if(!this."
                            + FIELD_CONTINUE
                            // if method called second player not present
                            // but entity still there
                            + "){ if(this."
                            + FIELD_DELAYED
                            + ")"
                            // process entity baseTick
                            + "{ this.player."
                            + getEntityBaseTickMethod()
                            // if FakePlayer is dead, continue normal disconnect
                            // TODO: use API method
                            + "(); if(this.player.dead) { this."
                            + FIELD_CONTINUE
                            + "=true;} "
                            + "return; }"
                            + "else {"
                            // first the disconnect delayed
                            + "this."
                            + FIELD_DELAYED
                            + "=true;"
                            // ask plugin if we can disconnect
                            + "if(!net.minecraft.server.DAPL_Injection.nmsDisconnectCall(this)) {"
                            + "return;}}}}");
            return cc.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // atm 1.5.1
    // TODO: implement dynamic way
    @Deprecated
    public static String findEntityBaseTickMethod() {
        return "x";
    }

    // TODO: implement dynamic way
    @Deprecated
    public static String findPlayerConnectionDisconnectMethodName() {
        return "a";
    }

    // TODO: implement dynamic way
    public static CtClass[] findPlayerConnectionDisconnectMethodParams() {
        ClassPool pool = ClassPool.getDefault();
        try {
            return new CtClass[] { pool.get(String.class.getName()),
                    pool.get(Object[].class.getName()) };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDaplInjectionJarName() {
        return "net/minecraft/server/" + getDaplInjectionName();
    }

    public static String getDaplInjectionName() {
        return "DAPL_Injection.class";
    }

    public static String getNmsClass() {
        return "PlayerConnection.class";
    }

    public void transform(File base, File newClass) {
        try {
            byte[] data = Files.readAllBytes(base.toPath());
            byte[] newData = transform(data);

            FileOutputStream out = new FileOutputStream(newClass);
            try {
                out.write(newData);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public void injectBukkit(File craftbukkitJar, File dest) {
        try {
            log("mcPackageVersion: " + getMcPackageVersion());
            log("entityBaseTickMethod: " + getEntityBaseTickMethod());
            log("playerConnectionDisconnectMethodName: "
                    + getPlayerConnectionDisconnectMethodName());

            JarFile bukkitJar = new JarFile(craftbukkitJar);

            // check if jar is injected
            boolean hasDaplInjection = DAPL_Utils.hasFile(bukkitJar,
                    getDaplInjectionJarName());
            if (hasDaplInjection) {
                log("always injected: " + craftbukkitJar);
                return;
            }

            // add current crafbukkit classes
            ClassPool.getDefault().insertClassPath(
                    craftbukkitJar.getAbsolutePath());

            // extract needed Files for transformation form craftbukkit jar
            String nmsClassName = getNmsClass();
            String nmsClassJarName = DAPL_Utils.getNmsPath(
                    getMcPackageVersion(), nmsClassName);
            File baseNmsClass = new File(dest, nmsClassName + ".bak");
            DAPL_Utils.deleteIfExists(baseNmsClass);
            DAPL_Utils.extractFromJar(bukkitJar, nmsClassJarName, baseNmsClass);

            // transform class
            File newNmsClass = new File(dest, nmsClassName);
            transform(baseNmsClass, newNmsClass);

            // extract injection class from installer
            File daplInjectionClass = new File(dest, getDaplInjectionName());
            if (!daplInjectionClass.exists()) {
                extractInjectionClass();
            }
            DAPL_Utils.copy(new File(getDaplInjectionName()),
                    daplInjectionClass);

            // pack all into new craftbukkit jar
            File newBukkitFile = new File(dest, "craftbukkit.jar");
            DAPL_Utils.deleteIfExists(newBukkitFile);
            HashMap<String, File> files = new HashMap<String, File>();
            // PlayerConnection.class
            files.put(nmsClassJarName, newNmsClass);
            // DAPL_Injection.class
            files.put(getDaplInjectionJarName(), daplInjectionClass);
            DAPL_Utils.addFilesToExistingJar(craftbukkitJar, newBukkitFile,
                    files);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractInjectionClass() {
        InputStream in = null;
        OutputStream out = null;
        try {
            System.out.println("extract from: " + getDaplInjectionJarName());
            in = this.getClass().getClassLoader().getResourceAsStream(
                    getDaplInjectionJarName());
            out = new FileOutputStream(getDaplInjectionName());
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMcPackageVersion() {
        return mcPackageVersion;
    }

    public void setMcPackageVersion(String mcPackageVersion) {
        this.mcPackageVersion = mcPackageVersion;
    }

    public String getPlayerConnectionDisconnectMethodName() {
        return playerConnectionDisconnectMethodName;
    }

    public void setPlayerConnectionDisconnectMethodName(
            String playerConnectionDisconnectMethodName) {
        this.playerConnectionDisconnectMethodName = playerConnectionDisconnectMethodName;
    }

    public String getEntityBaseTickMethod() {
        return entityBaseTickMethod;
    }

    public void setEntityBaseTickMethod(String entityBaseTickMethod) {
        this.entityBaseTickMethod = entityBaseTickMethod;
    }
}
