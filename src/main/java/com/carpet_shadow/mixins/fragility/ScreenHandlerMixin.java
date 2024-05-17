package com.carpet_shadow.mixins.fragility;

import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.carpet_shadow.interfaces.ShifingItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
  @Shadow
  public abstract ItemStack getCursorStack();

  @Inject(method = "internalOnSlotClick", at = @At("HEAD"))
  public void merging_start(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
    Globals.mergingThreads.add(Thread.currentThread());
  }

  @Inject(method = "internalOnSlotClick", at = @At("RETURN"))
  public void merging_end(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
    Globals.mergingThreads.remove(Thread.currentThread());
  }

  @WrapOperation(method = "internalOnSlotClick", slice = @Slice(
    from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z", ordinal = 1)),
    at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 1)
  )
  public void remove_shadow_stack(ScreenHandler instance, ItemStack stack, Operation<Void> original) {
    String shadowId1 = ((ShadowItem) (Object) getCursorStack()).carpet_shadow$getShadowId();
    String shadowId2 = ((ShadowItem) (Object) stack).carpet_shadow$getShadowId();
    if (CarpetShadowSettings.shadowItemInventoryFragilityFix && shadowId1 != null && shadowId1.equals(shadowId2)) {
      instance.setCursorStack(ItemStack.EMPTY);
    } else {
      original.call(instance, stack);
    }
  }

//  TODO feat this
//  @WrapOperation(method = "internalOnSlotClick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z")),
//    at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"))
//  public ItemStack fix_shift(ScreenHandler instance, PlayerEntity player, int index, Operation<ItemStack> original) {
//    if (CarpetShadowSettings.shadowItemInventoryFragilityFix) {
//      Slot og = instance.slots.get(index);
//      ItemStack og_item = og.getStack();
//      if (((ShadowItem) (Object) og_item).carpet_shadow$getShadowId() != null) {
//        ItemStack mirror = og_item.copy();
//        ((ShadowItem) (Object) mirror).carpet_shadow$setShadowId(((ShadowItem) (Object) og_item).carpet_shadow$getShadowId());
//        og.setStack(mirror);
//        ((ShifingItem) (Object) mirror).carpet_shadow$setShiftMoving(true);
//        ItemStack ret = original.call(instance, player, index);
//        ((ShifingItem) (Object) mirror).carpet_shadow$setShiftMoving(false);
//        if (ret == ItemStack.EMPTY) {
//          og_item = Globals.getByIdOrAdd(((ShadowItem) (Object) og_item).carpet_shadow$getShadowId(), og_item);
//          og.setStack(og_item);
//          og_item.setCount(mirror.getCount());
//        }
//        return ret;
//      }
//    }
//    return original.call(instance, player, index);
//  }

  @WrapOperation(method = "insertItem", slice = @Slice(
    from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 1)
  ),
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;", ordinal = 1))
  public ItemStack fix_shift(ItemStack instance, int amount, Operation<ItemStack> original) {
    if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ((ShadowItem) (Object) instance).carpet_shadow$getShadowId() != null) {
      String shadow_id = ((ShadowItem) (Object) instance).carpet_shadow$getShadowId();
      ItemStack og_item = Globals.getByIdOrNull(shadow_id);
      if (og_item != null) {
        og_item.setCount(instance.getCount());
        instance.setCount(0);
        return og_item;
      }
    }
    return original.call(instance, amount);
  }

  @WrapOperation(method = "insertItem",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0))
  public boolean fix_shift2(ItemStack instance, Operation<Boolean> original) {
    if (CarpetShadowSettings.shadowItemInventoryFragilityFix && ((ShifingItem) (Object) instance).carpet_shadow$isShiftMoving()) {
      return true;
    }
    return original.call(instance);
  }

  @WrapOperation(method = "internalOnSlotClick",
    slice = @Slice(
      from = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"),
      to = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z")
    ),
    at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;canInsertItemIntoSlot(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/item/ItemStack;Z)Z"))
  public boolean fixQuickCraft(Slot slot, ItemStack stack, boolean allowOverflow, Operation<Boolean> original) {
    if (CarpetShadowSettings.shadowItemInventoryFragilityFix) {
      ItemStack slotStack = slot.getStack();
      ItemStack ref1 = Globals.getByIdOrNull(((ShadowItem) (Object) slotStack).carpet_shadow$getShadowId());
      ItemStack ref2 = Globals.getByIdOrNull(((ShadowItem) (Object) stack).carpet_shadow$getShadowId());
      if (slotStack == ref1 || stack == ref2)
        return false;
    }
    return original.call(slot, stack, allowOverflow);
  }
}
