package com.github.idragonfire.DragonAntiPvPLeaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

public class DAPL_Transformer {
    public static final String FIELD_DELAYED = "dragonfire_dapl_delay_disconnect";
    public static final String FIELD_CONTINUE = "dragonfire_dapl_continue_disconnect";

    public static byte[] transform(byte[] data) {
        try {
            CtClass cc = ClassPool.getDefault().makeClass(
                    new java.io.ByteArrayInputStream(data));
            cc.addField(CtField.make("public boolean " + FIELD_DELAYED
                    + " = false;", cc));
            cc.addField(CtField.make("public boolean " + FIELD_CONTINUE
                    + " = false;", cc));
            CtMethod disconnectMethod = cc.getDeclaredMethod(
                    findPlayerConnectionDisconnectMethodName(),
                    findPlayerCOnnectionDisconnectMethodParams());
            disconnectMethod
                    .insertBefore(
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
                            + findEntityBaseTickMethod()
                            // if FakePlayer is dead, continue normal disconnect
                            + "(); if(this.player.dead) { this."
                            + FIELD_CONTINUE
                            + "=true;} "
                            + "return; }"
                            + "else {"
                            // first the disconnect delyed
                            + "this." + FIELD_DELAYED + "=true;"
                            // ask plugin if we can disconnect
                            + "if(!net.minecraft.server.DAPL_Injection.nmsDisconnectCall(this)) {"
                            + "return;}}}}");
            return cc.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: implement dynmaic way
    public static String findEntityBaseTickMethod() {
        return "x";
    }
    
    //TODO: implement dynmaic way
    public static String findPlayerConnectionDisconnectMethodName() {
        return "a";
    }
    
    //TODO: implement dynmaic way
    public static CtClass[] findPlayerCOnnectionDisconnectMethodParams() {
        ClassPool pool = ClassPool.getDefault();
        try {
            return new CtClass[] { pool.get(String.class.getName()),
                    pool.get(Object[].class.getName()) };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            File f = new File("C:\\PlayerConnection.class");
            byte[] data = Files.readAllBytes(f.toPath());
            byte[] newData = transform(data);

            FileOutputStream out = new FileOutputStream(new File(
                    "C:\\new\\PlayerConnection.class"));
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
}
