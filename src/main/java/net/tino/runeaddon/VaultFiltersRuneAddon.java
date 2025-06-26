package net.tino.runeaddon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tino.runeaddon.attributes.rune.*;
import net.tino.runeaddon.ModPresence;

@Mod(VaultFiltersRuneAddon.MOD_ID)
public class VaultFiltersRuneAddon {
    public static final String MOD_ID = "vaultfiltersruneaddon";
    public static final String MOD_VERSION = "1.21.1-addon-2";

    public VaultFiltersRuneAddon() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModPresence.class);
    }

    private void setup(FMLCommonSetupEvent event) {
        ModPresence.init();
        // Register your custom attributes with dummy/default values!
        new BossRuneModifierAttribute("Strength").register(BossRuneModifierAttribute::new);
        new BossRuneGivesItemAttribute("the_vault:helmet").register(BossRuneGivesItemAttribute::new);
        new BossRuneGearRarityAttribute("Omega").register(BossRuneGearRarityAttribute::new);
        new BossRuneBoosterPackTypeAttribute("the_vault:mega_stat_pack").register(BossRuneBoosterPackTypeAttribute::new);
        new BossRuneInscriptionTypeAttribute("Laboratory").register(BossRuneInscriptionTypeAttribute::new);
    }
}
