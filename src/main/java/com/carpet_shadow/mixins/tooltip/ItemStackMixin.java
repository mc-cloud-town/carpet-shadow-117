package com.carpet_shadow.mixins.tooltip;


import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

  @Inject(method = "getTooltip", at = @At("RETURN"))
  private void postToolTip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
    List<Text> list = cir.getReturnValue();
    if (CarpetShadowSettings.shadowItemTooltip && ((ShadowItem) this).carpet_shadow$getClientShadowId() != null) {
      MutableText text = new LiteralText("shadow_id: ");
      MutableText sub = new LiteralText(((ShadowItem) this).carpet_shadow$getClientShadowId());
      sub.formatted(Formatting.GOLD, Formatting.BOLD);
      text.append(sub);
      text.formatted(Formatting.ITALIC);
      list.add(text);
    }
  }
}
