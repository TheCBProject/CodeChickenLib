package codechicken.lib.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;

public class ModDescriptionEnhancer {

    private static boolean hasInit = false;
    private static Map<String, List<String>> supporters = new HashMap<>();
    private static Map<String, ModMetadata> mods = new HashMap<>();
    private static long lastDownload = 0;
    private static boolean shouldDownload;

    public static void init() {
        if (hasInit) {
            return;
        }
        hasInit = true;
        File marker = new File("config/codechicken/supporters.marker");
        File supporters_file = new File("config/codechicken/supporters.json");
        shouldDownload = !supporters_file.exists();
        Date date = new Date();
        if (marker.exists()) {
            try {
                FileReader reader = new FileReader(marker);
                lastDownload = Long.valueOf(IOUtils.toString(reader));
                IOUtils.closeQuietly(reader);
            } catch (IOException e) {
                CCLLog.log(Level.WARN, "Error reading supporters marker file. Deleting..");
                marker.delete();
                lastDownload = 0;
            }
        } else {
            shouldDownload = true;
            try {
                if (!marker.getParentFile().exists()) {
                    marker.getParentFile().mkdirs();
                }
                if (!marker.exists()) {
                    marker.createNewFile();
                }
                PrintWriter writer = new PrintWriter(new FileOutputStream(marker));
                writer.print(date.getTime());
                writer.flush();
                IOUtils.closeQuietly(writer);
            } catch (IOException e) {
                CCLLog.log(Level.WARN, e, "Error writing supporters marker file.");
            }
        }

        Thread thread = new Thread(() -> {
            try {
                if (shouldDownload || date.getTime() >= lastDownload + (1000 * 60 * 60 * 24)) {
                    if (!supporters_file.exists()) {
                        supporters_file.createNewFile();
                    }
                    URL url = new URL("http://chickenbones.net/Files/supporters.json");
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("User-Agent", "CC Supporters Downloader.");
                    InputStream is = connection.getInputStream();
                    OutputStream fos = new FileOutputStream(supporters_file);
                    IOUtils.copy(is, fos);
                    IOUtils.closeQuietly(is, fos);
                }
                parse(supporters_file);
                applySupporters();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.setName("CC Supporters Downloader");
        thread.start();
    }

    private static void parse(File supporters_file) throws IOException {
        JsonReader reader = new JsonReader(new FileReader(supporters_file));
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(reader).getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            String mod = object.get("mod").getAsString();
            List<String> supporters = new ArrayList<>();
            for (JsonElement s : object.get("supporters").getAsJsonArray()) {
                supporters.add(s.getAsString());
            }
            ModDescriptionEnhancer.supporters.put(mod, supporters);
        }
    }

    private static void applySupporters() {
        for (Entry<String, List<String>> entry : supporters.entrySet()) {
            if (mods.containsKey(entry.getKey())) {
                ModMetadata metadata = mods.get(entry.getKey());
                String supporters_string = generateList(entry.getValue());
                if (metadata.description.contains("<supporters>")) {
                    metadata.description = metadata.description.replace("<supporters>", supporters_string);
                } else {
                    metadata.description = metadata.description + supporters_string;
                }
            }
        }
    }

    private static String generateList(List<String> supporters) {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(TextFormatting.GOLD).append("Supporters:");
        for (String supporter : supporters) {
            builder.append("\n").append("    ");
            builder.append(TextFormatting.AQUA).append(supporter);
        }
        return builder.toString();
    }

    public static void registerEnhancement(String mod_id, String json_name) {
        mods.put(json_name, FMLCommonHandler.instance().findContainerFor(mod_id).getMetadata());
    }

    @Deprecated
    public static void enhanceMod(Object mod) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        mc.getMetadata().description = enhanceDesc(mc.getMetadata().description);
    }

    @Deprecated
    public static String enhanceDesc(String desc) {
        int supportersIdx = desc.indexOf("Supporters:");
        if (supportersIdx < 0) {
            return desc;
        }

        String supportersList = desc.substring(supportersIdx);
        supportersList = supportersList.replaceAll("\\b(\\w+)\\b", TextFormatting.AQUA + "$1");
        return desc.substring(0, supportersIdx) + supportersList;
    }
}
