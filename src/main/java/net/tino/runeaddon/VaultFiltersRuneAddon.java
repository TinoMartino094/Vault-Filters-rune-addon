package net.tino.runeaddon;

import net.tino.runeaddon.attributes.rune.BossRuneBoosterPackTypeAttribute;
import net.tino.runeaddon.attributes.rune.BossRuneGearRarityAttribute;
import net.tino.runeaddon.attributes.rune.BossRuneGivesItemAttribute;
import net.tino.runeaddon.attributes.rune.BossRuneInscriptionTypeAttribute;
import net.tino.runeaddon.attributes.rune.BossRuneModifierAttribute;
import net.minecraftforge.fml.common.Mod;

@Mod("vaultfiltersruneaddon")
public class VaultFiltersRuneAddon {
    public VaultFiltersRuneAddon() {
        // Register your custom attributes with dummy/default values!
        new BossRuneModifierAttribute("Strength").register(BossRuneModifierAttribute::new);
        new BossRuneGivesItemAttribute("the_vault:helmet").register(BossRuneGivesItemAttribute::new);
        new BossRuneGearRarityAttribute("Omega").register(BossRuneGearRarityAttribute::new);
        new BossRuneBoosterPackTypeAttribute("the_vault:mega_stat_pack").register(BossRuneBoosterPackTypeAttribute::new);
        new BossRuneInscriptionTypeAttribute("Laboratory").register(BossRuneInscriptionTypeAttribute::new);
    }
}
