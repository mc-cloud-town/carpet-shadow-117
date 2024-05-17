package com.carpet_shadow.mixins.inv_updates.loaders;


import net.minecraft.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
  //  TODO feat this
//  @Redirect(method = "writeNbtToBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;readNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
//  private static void interceptBlockEntityLoad(BlockEntity instance, NbtCompound nbt) {
//    InventoryItem.readNbt(instance, nbt);
//  }
}
