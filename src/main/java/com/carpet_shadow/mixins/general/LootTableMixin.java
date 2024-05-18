package com.carpet_shadow.mixins.general;

import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootTable.class)
public class LootTableMixin {
  @Redirect(method = "method_331", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCount(I)V"))
  private static void fix_survival_shulkers(ItemStack itemStack, int count) {
    String shadowId = ((ShadowItem) (Object) itemStack).carpet_shadow$getShadowId();

    if (!(shadowId != null && itemStack.getCount() == count)) {
      itemStack.setCount(count);
    }
  }
}
