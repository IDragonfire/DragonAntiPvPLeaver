package com.github.idragonfire.dapl_installer.ui;

import java.io.File;
import java.util.jar.JarFile;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.idragonfire.dapl_installer.DAPL_Transformer;
import com.github.idragonfire.dapl_installer.DAPL_Utils;

public class MainFrame {
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        setLookAndFeel();
        // ask for craftbukkit
        JFileChooser chooserCrafbukkitJar = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "craftbukkit", "jar");
        chooserCrafbukkitJar.setFileFilter(filter);
        chooserCrafbukkitJar.setDialogTitle("Select crafbukkit file");
        int returnValCraftbukkit = chooserCrafbukkitJar.showOpenDialog(null);
        if (returnValCraftbukkit != JFileChooser.APPROVE_OPTION) {
            System.out.println("no craftbukkit file selected");
            return;
        }

        // ask for dest file
        JFileChooser chooserDest = new JFileChooser();
        chooserDest.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooserDest.setDialogTitle("Select destination folder");
        int returnValDest = chooserDest.showOpenDialog(null);
        if (returnValDest != JFileChooser.APPROVE_OPTION) {
            System.out.println("no dest folder");
            return;
        }

        File craftbukkitFile = chooserCrafbukkitJar.getSelectedFile();
        File destFolder = chooserDest.getSelectedFile();

        DAPL_Transformer transformer = new DAPL_Transformer();

        try {
            JarFile crafbukkitTmpFile = new JarFile(craftbukkitFile);
            String mc_version = DAPL_Utils
                    .getMcPackageVersion(crafbukkitTmpFile);
            transformer.setMcPackageVersion(mc_version);
        } catch (Exception e) {
            e.printStackTrace();
        }

        transformer.setEntityBaseTickMethod(DAPL_Transformer
                .findEntityBaseTickMethod());
        transformer.setPlayerConnectionDisconnectMethodName(DAPL_Transformer
                .findPlayerConnectionDisconnectMethodName());

        transformer.injectBukkit(craftbukkitFile, destFolder);
        JOptionPane.showMessageDialog(null, "Done !");
    }
}
