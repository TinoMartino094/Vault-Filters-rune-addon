package net.tino.runeaddon.mixin.compat.create;

import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import net.tino.runeaddon.ModPresence;
import net.tino.runeaddon.attributes.rune.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(AttributeFilterScreen.class)
public class MixinAttributeFilterScreen {
    @Redirect(
        method = "referenceItemChanged",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/filter/ItemAttribute;listAttributesOf(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/List;"
        ),
        remap = false
    )
    public List<ItemAttribute> runeAddon$listAttributes(ItemAttribute instance, ItemStack stack, Level world) {
        if (!ModPresence.serverHasRuneAddon()) {
            // Filter out all rune addon attributes if server presence is missing
            return instance.listAttributesOf(stack, world).stream()
                .filter(attr -> !(attr instanceof BossRuneModifierAttribute
                               || attr instanceof BossRuneGivesItemAttribute
                               || attr instanceof BossRuneGearRarityAttribute
                               || attr instanceof BossRuneBoosterPackTypeAttribute
                               || attr instanceof BossRuneInscriptionTypeAttribute))
                .toList();
        }
        return instance.listAttributesOf(stack, world);
    }
}
