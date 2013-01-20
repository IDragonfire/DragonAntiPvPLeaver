package com.github.idragonfire.DragonAntiPvPLeaver.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Inspired by md_5
 * 
 * An awesome super-duper-lazy Config lib! Just extend it, set some (non-static) variables
 * 
 * @author codename_B
 * @version 2.1
 */
public abstract class Config {

    private transient File file = null;
    private transient YamlConfiguration conf = new YamlConfiguration();

    /**
     * Must be called before using config.load() or config.save();
     * 
     * @param input
     * @return (Config) instance
     */
    public Config setFile(Object input) {
        // handle the File
        if (input == null) {
            new InvalidConfigurationException("File cannot be null!")
                    .printStackTrace();
        } else if (input instanceof File) {
            // the file, directly
            this.file = (File) input;
        } else if (input instanceof Plugin) {
            // the config.yml of the plugin
            this.file = getFile((Plugin) input);
        } else if (input instanceof String) {
            // the literal file from the string
            this.file = new File((String) input);
        }
        return this;
    }

    /**
     * Lazy load
     */
    public void load() {
        if (this.file != null) {
            try {
                onLoad(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new InvalidConfigurationException("File cannot be null!")
                    .printStackTrace();
        }
    }

    /**
     * Lazy save
     */
    public void save() {
        if (this.file != null) {
            try {
                onSave(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new InvalidConfigurationException("File cannot be null!")
                    .printStackTrace();
        }
    }

    /**
     * Internal method - used by load();
     * 
     * @param plugin
     * @throws Exception
     */
    private void onLoad(File file) throws Exception {
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        this.conf.load(file);
        for (Field field : getClass().getDeclaredFields()) {
            String path = field.getName().replaceAll("_", ".");
            if (doSkip(field)) {
                // don't touch it
                continue;
            }
            if (this.conf.isSet(path)) {
                field.set(this, toBukkit(this.conf.get(path), field, path));
            } else {
                this.conf.set(path, toConfig(field.get(this), field, path));
            }
        }
        this.conf.save(file);
    }

    /**
     * Internal method - used by save();
     * 
     * @param plugin
     * @throws Exception
     */
    private void onSave(File file) throws Exception {
        HashMap<String, String[]> annos = new HashMap<String, String[]>();
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        for (Field field : getClass().getDeclaredFields()) {
            String path = field.getName().replaceAll("_", ".");
            if (doSkip(field)) {
                // don't touch it
                continue;
            } else {
                onComment(annos, path, field);
                this.conf.set(path, toConfig(field.get(this), field, path));
            }
        }
        this.conf.save(file);
        try {
            commentPostProcess(file, annos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onComment(HashMap<String, String[]> annos, String path,
            Field field) {
        if (field.getAnnotation(Comment.class) != null) {
            annos.put(path, new String[] { field.getAnnotation(Comment.class)
                    .value() });
            return;
        }
        if (field.getAnnotation(MultiComment.class) != null) {
            annos.put(path, field.getAnnotation(MultiComment.class).value());
        }

    }

    private int level(String path) {
        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) != ' ') {
                break;
            }
            count++;
        }
        return count / 2;
    }

    private void commentPostProcess(File file, HashMap<String, String[]> annos)
            throws IOException {
        ArrayList<String> buffer = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.add(line);
        }
        reader.close();
        ArrayList<String> key = new ArrayList<String>();
        int level;
        String newKey;
        String tmpKey;
        int diff;
        String[] comment;
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < buffer.size(); i++) {
            if (buffer.get(i).charAt(0) == '#') {
                continue;
            }
            newKey = buffer.get(i).split(":")[0];
            level = level(newKey);
            newKey = newKey.trim();
            if (level == 0) {
                key.clear();
                key.add(newKey);
            } else if (key.size() < level) {
                key.add(newKey);

            } else {
                diff = key.size() - level;
                for (int j = 0; j < diff; j++) {
                    key.remove(key.size() - 1);
                }
                tmpKey = StringUtils.join(key, ".");
                tmpKey += "." + newKey;

                if (annos.containsKey(tmpKey)) {
                    comment = annos.get(tmpKey);
                    for (int j = 0; j < comment.length; j++) {
                        writer.write("# " + comment[j]);
                        writer.newLine();
                    }
                }
            }
            writer.write(buffer.get(i));
            writer.newLine();
        }
        writer.close();
    }

    /*
     * Main conversion methods
     */

    private Object toBukkit(Object in, Field field, String path)
            throws Exception {
        if (isConfigurationSection(in)) {
            return getMap((ConfigurationSection) in, field, path);
        } else if (isJSON(in)) {
            return getLocation((String) in);
        } else if(in instanceof ArrayList<?>){
            return ((ArrayList<?>) in).toArray(new String[0]);
        } else {
            return in;
        }
    }

    @SuppressWarnings( { "unchecked" })
    private Object toConfig(Object out, Field field, String path)
            throws Exception {
        if (isMap(out)) {
            return getMap((Map) out, field, path);
        } else if (isLocation(out)) {
            return getLocation((Location) out);
        } else {
            return out;
        }
    }

    /*
     * Checkers
     */

    private boolean isJSON(Object o) {
        try {
            if (o instanceof String) {
                String s = (String) o;
                if (s.startsWith("{")) {
                    return new JSONParser().parse(s) != null;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isMap(Object o) {
        try {
            return (Map) o != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLocation(Object o) {
        try {
            return (Location) o != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isConfigurationSection(Object o) {
        try {
            return (ConfigurationSection) o != null;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Converters
     */

    @SuppressWarnings( { "unchecked" })
    private ConfigurationSection getMap(Map data, Field field, String path)
            throws Exception {
        ConfigurationSection cs = this.conf.createSection(path);
        Set<String> keys = data.keySet();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                System.out.println("keys");
                Object out = data.get(key);
                System.out.println(out.getClass().getName());
                out = toConfig(out, field, path);
                System.out.println(out.getClass().getName());
                cs.set(key, out);
            }
            return cs;
        }
        return cs;
    }

    @SuppressWarnings( { "unchecked" })
    private Map getMap(ConfigurationSection data, Field field, String path)
            throws Exception {
        Set<String> keys = data.getKeys(false);
        Map map = new HashMap();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Object in = data.get(key);
                in = toBukkit(in, field, path);
                map.put(key, in);
            }
            return map;
        }
        return map;
    }

    private Location getLocation(String json) throws Exception {
        JSONObject data = (JSONObject) new JSONParser().parse(json);
        // world
        World world = Bukkit.getWorld((String) data.get("world"));
        // x, y, z
        double x = Double.parseDouble((String) data.get("x"));
        double y = Double.parseDouble((String) data.get("y"));
        double z = Double.parseDouble((String) data.get("z"));
        // pitch, yaw
        float pitch = Float.parseFloat((String) data.get("pitch"));
        float yaw = Float.parseFloat((String) data.get("yaw"));
        // generate Location
        Location loc = new Location(world, x, y, z);
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        return loc;
    }

    @SuppressWarnings("unchecked")
    private String getLocation(Location loc) {
        JSONObject data = new JSONObject();
        // world
        data.put("world", loc.getWorld().getName());
        // x, y, z
        data.put("x", String.valueOf(loc.getX()));
        data.put("y", String.valueOf(loc.getY()));
        data.put("z", String.valueOf(loc.getZ()));
        // pitch, yaw
        data.put("pitch", String.valueOf(loc.getPitch()));
        data.put("yaw", String.valueOf(loc.getYaw()));
        return data.toJSONString();
    }

    /*
     * Utility methods
     */

    /**
     * A little internal method to save re-using code
     * 
     * @param field
     * @return skip
     */
    private boolean doSkip(Field field) {
        return Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers());
    }

    private File getFile(Plugin plugin) {
        return new File(plugin.getDataFolder(), "config.yml");
    }

}