package net.johnpgr.craftingtableiifabric.description;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.johnpgr.craftingtableiifabric.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CraftingTableIIDescriptions {
    private static final String FALLBACK_LANG = "en_us";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();

    public static Map<String, String> descriptionsDict = new HashMap<>();
    private static boolean loaded = false;

    private CraftingTableIIDescriptions() {
    }

    public static File descriptionFile(String lang) {
        return new File(
                Services.PLATFORM.getConfigDirectory(),
                CraftingTableII.MOD_ID + File.separator + "descriptions" + File.separator + lang + ".json"
        );
    }

    public static void register() {
        Services.CLIENT.onClientStarted(() -> {
            if (!loaded) {
                load(Minecraft.getInstance());
                loaded = true;
            }
        });
    }

    public static void ensureLoaded(Minecraft client) {
        if (!loaded) {
            load(client);
            loaded = true;
        }
    }

    public static void load(Minecraft client) {
        CraftingTableII.LOGGER.info("[{}] Trying to read descriptions file...", CraftingTableII.MOD_ID);
        var resourceManager = client.getResourceManager();
        String currentLang = client.getLanguageManager().getSelected();
        Optional<Resource> descriptionResource = resourceManager.getResource(
                ResourceLocation.fromNamespaceAndPath(CraftingTableII.MOD_ID, "descriptions/" + currentLang + ".json")
        );

        if (descriptionResource.isEmpty()) {
            currentLang = FALLBACK_LANG;
            descriptionResource = resourceManager.getResource(
                    ResourceLocation.fromNamespaceAndPath(CraftingTableII.MOD_ID, "descriptions/" + FALLBACK_LANG + ".json")
            );
        }

        if (descriptionResource.isEmpty()) {
            CraftingTableII.LOGGER.error("[{}] Failed to load descriptions", CraftingTableII.MOD_ID);
            return;
        }

        descriptionsDict = toDescriptionsDict(descriptionResource.get());

        try {
            File descriptionsFile = descriptionFile(currentLang);
            if (descriptionsFile.getParentFile() != null) {
                descriptionsFile.getParentFile().mkdirs();
            }
            if (descriptionsFile.createNewFile()) {
                CraftingTableII.LOGGER.info("[{}] No descriptions file found, creating a new one...", CraftingTableII.MOD_ID);
                writeToFile(descriptionsDict, descriptionsFile);
                CraftingTableII.LOGGER.info("[{}] Successfully created default descriptions file.", CraftingTableII.MOD_ID);
            } else {
                CraftingTableII.LOGGER.info("[{}] A descriptions file was found, loading it..", CraftingTableII.MOD_ID);
                descriptionsDict = readFromFile(descriptionsFile);
                CraftingTableII.LOGGER.info("[{}] Successfully loaded descriptions file.", CraftingTableII.MOD_ID);
            }
        } catch (IOException ex) {
            CraftingTableII.LOGGER.error(
                    "[{}] There was an error creating/loading the descriptions file!",
                    CraftingTableII.MOD_ID,
                    ex
            );
        }
    }

    private static Map<String, String> toDescriptionsDict(Resource resource) {
        try (InputStream inputStream = resource.open()) {
            return GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), MAP_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToFile(Map<String, String> map, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(GSON.toJson(map));
        }
    }

    private static Map<String, String> readFromFile(File file) throws IOException {
        try (var reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, MAP_TYPE);
        }
    }
}
