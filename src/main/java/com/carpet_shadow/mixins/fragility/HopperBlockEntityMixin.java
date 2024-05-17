package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.interfaces.ShadowItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
  @WrapOperation(method = "extract(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/entity/ItemEntity;)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
  private static ItemStack extract_fix_entity(ItemStack instance, Operation<ItemStack> original) {
    if (CarpetShadowSettings.shadowItemTransferFragilityFix && ((ShadowItem) (Object) instance).carpet_shadow$getShadowId() != null) {
      return instance;
    }
    return original.call(instance);
  }

  @WrapOperation(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
  private static ItemStack extract_fix_storage1(ItemStack instance, Operation<ItemStack> original) {
    if (CarpetShadowSettings.shadowItemTransferFragilityFix && ((ShadowItem) (Object) instance).carpet_shadow$getShadowId() != null) {
      return instance;
    }
    return original.call(instance);
  }

  @WrapOperation(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
  private static void extract_fix_storage2(Inventory instance, int i, ItemStack itemStack, Operation<Void> original) {
    if (CarpetShadowSettings.shadowItemTransferFragilityFix && ((ShadowItem) (Object) itemStack).carpet_shadow$getShadowId() != null) {
      itemStack.setCount(itemStack.getCount() + 1);
    }
    original.call(instance, i, itemStack);
  }

  @WrapOperation(method = "insert", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
  private static ItemStack insert_fix_storage1(ItemStack instance, Operation<ItemStack> original) {
    if (CarpetShadowSettings.shadowItemTransferFragilityFix && ((ShadowItem) (Object) instance).carpet_shadow$getShadowId() != null) {
      return instance;
    }
    return original.call(instance);
  }

  @WrapOperation(method = "insert",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
  private static void insert_fix_storage2(Inventory instance, int i, ItemStack itemStack, Operation<Void> original) {
    if (CarpetShadowSettings.shadowItemTransferFragilityFix && ((ShadowItem) (Object) itemStack).carpet_shadow$getShadowId() != null) {
      itemStack.setCount(itemStack.getCount() + 1);
    }
    original.call(instance, i, itemStack);
  }
}
