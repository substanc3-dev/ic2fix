package dev.substanc3.ic2fix.mixin;

import com.google.common.base.Charsets;
import ic2.core.init.Localization;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

// Loads legacy IC2 translation file to repair a lot of missing strings
@Mixin(TranslationStorage.class)
@SuppressWarnings({"unchecked"})
public class TranslationStorageMixin {
    @ModifyArgs(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;)Lnet/minecraft/client/resource/language/TranslationStorage;", at=@At(value="INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;", remap = false))
    private static void injectTranslations(Args args)
    {
        // Get the processed list of minecraft-parsed strings to append to
        var map = (Map<String, String>)args.get(0);

        // Load the IC2 translation resource file
        var stream = Localization.class.getResourceAsStream("/assets/ic2/lang_ic2/en_us.properties");
        Properties props = new Properties();
        try {
            props.load(new InputStreamReader(stream, Charsets.UTF_8));

            // Iterate over strings in the IC2 translations and place them in the map
            for (Map.Entry<Object, Object> entries : props.entrySet()) {
                Object key = entries.getKey();
                Object value = entries.getValue();
                if (key instanceof String && value instanceof String) {
                    String newKey = "ic2." + (String)key;
                    map.put(newKey, (String)value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Place the corrected map into the arguments again
        args.set(0, map);
    }
}
