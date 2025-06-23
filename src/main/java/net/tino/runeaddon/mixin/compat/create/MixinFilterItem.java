package net.tino.runeaddon.mixin.compat.create;

import com.simibubi.create.content.logistics.filter.FilterItem;
import net.tino.runeaddon.ModPresence;
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
        if (!ModPresence.PLAYERS_WITH_RUNE_ADDON.contains(player.getUUID())) {
            // Optionally show a message here
            cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
        }
    }
}
