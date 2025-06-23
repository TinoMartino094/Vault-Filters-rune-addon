package net.tino.runeaddon.mixin.compat.create;

import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.tino.runeaddon.ModPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(value = FilterScreen.class, remap = false)
public abstract class MixinFilterScreen {
    @Inject(method = "getTooltipButtons", at = @At("HEAD"), cancellable = true, remap = false)
    private void runeAddon$addToolTipButtons(CallbackInfoReturnable<List<IconButton>> cir) {
        if (!ModPresence.serverHasRuneAddon()) {
            cir.setReturnValue(List.of());
        }
    }

    @Inject(method = "getTooltipDescriptions", at = @At("HEAD"), cancellable = true, remap = false)
    private void runeAddon$addToolTipDescriptions(CallbackInfoReturnable<List<?>> cir) {
        if (!ModPresence.serverHasRuneAddon()) {
            cir.setReturnValue(List.of());
        }
    }

    @Inject(method = "getIndicators", at = @At("HEAD"), cancellable = true, remap = false)
    private void runeAddon$addIndicators(CallbackInfoReturnable<List<Indicator>> cir) {
        if (!ModPresence.serverHasRuneAddon()) {
            cir.setReturnValue(List.of());
        }
    }
}
