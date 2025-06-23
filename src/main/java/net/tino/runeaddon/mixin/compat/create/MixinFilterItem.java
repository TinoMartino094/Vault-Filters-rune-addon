package net.tino.runeaddon.mixin.compat.create;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import net.tino.runeaddon.ModPresence;
import net.tino.runeaddon.VaultFiltersRuneAddon;
import net.tino.runeaddon.attributes.rune.*;
import net.joseph.vaultfilters.attributes.abstracts.StringAttribute;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilterItem.class)
public class MixinFilterItem {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void runeAddon$use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (ModPresence.PLAYERS_WITH_RUNE_ADDON.contains(player.getUUID())) {
            return;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (player.isShiftKeyDown()
                || hand != InteractionHand.MAIN_HAND
                || world.isClientSide
                || !(player instanceof ServerPlayer)
                || !heldItem.hasTag()) {
            return;
        }

        Component msg = new TextComponent(
            "This filter has Vault Filters Rune Addon features selected on it. " +
            "Install Vault Filters Rune Addon version " + VaultFiltersRuneAddon.MOD_VERSION +
            " to open the UI (Sneaky mad Tino >:c)"
        ).withStyle(ChatFormatting.RED);

        CompoundTag tag = heldItem.getTag();
        // Check if the filter has your rune attributes (stored as StringAttribute)
        ListTag attributes = tag.getList("MatchedAttributes", CompoundTag.TAG_COMPOUND);
        for (Tag attribute : attributes) {
            if (attribute instanceof CompoundTag compound) {
                ItemAttribute attr = ItemAttribute.fromNBT(compound);
                if (attr instanceof BossRuneModifierAttribute
                        || attr instanceof BossRuneGivesItemAttribute
                        || attr instanceof BossRuneGearRarityAttribute
                        || attr instanceof BossRuneBoosterPackTypeAttribute
                        || attr instanceof BossRuneInscriptionTypeAttribute) {
                    player.displayClientMessage(msg, false);
                    cir.setReturnValue(InteractionResultHolder.pass(heldItem));
                    break;
                }
            }
        }
    }
}
