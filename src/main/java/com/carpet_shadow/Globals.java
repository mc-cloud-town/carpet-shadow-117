package com.carpet_shadow;

import com.carpet_shadow.interfaces.ShadowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class Globals {

    public static ItemStack getByIdOrNull (String shadow_id){
        Reference<ItemStack> reference = CarpetShadow.shadowMap.get(shadow_id);
        if (reference != null && !reference.refersTo(null)){
            return reference.get();
        }
        return null;
    }

    public static ItemStack getByIdOrAdd (String shadow_id, ItemStack stack){
        Reference<ItemStack> reference = CarpetShadow.shadowMap.get(shadow_id);
        if (reference != null && !reference.refersTo(null)){
            return reference.get();
        }
        CarpetShadow.shadowMap.put(shadow_id,new WeakReference<>(stack));
        return stack;
    }


    public static void shadow_merge_check(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir){
        if(CarpetShadowSettings.shadowItemFragilityFixes && cir.getReturnValue()){
            String shadow1 = ((ShadowItem)(Object)stack1).getShadowId();
            String shadow2 = ((ShadowItem)(Object)stack2).getShadowId();
            if(CarpetShadowSettings.shadowItemPreventCombine){
                if(shadow1!=null && shadow2!=null)
                    cir.setReturnValue(false);
            }else{
                if (shadow1 != null && shadow1.equals(shadow2))
                    cir.setReturnValue(false);
            }
        }
    }
}
