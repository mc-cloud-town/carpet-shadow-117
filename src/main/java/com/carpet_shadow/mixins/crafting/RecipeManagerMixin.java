package com.carpet_shadow.mixins.crafting;

import com.carpet_shadow.CarpetShadow;
import com.carpet_shadow.CarpetShadowSettings;
import com.carpet_shadow.Globals;
import com.carpet_shadow.interfaces.ShadowItem;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@SuppressWarnings({"unchecked","rawtypes"})
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addShadowRecipe(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map map2, ImmutableMap.Builder<Identifier, Recipe<?>> builder){
        Identifier identifier = new Identifier("carpet_shadow","shadow_recipe");
        Recipe<?> recipe = new ShapelessRecipe(identifier, "shadow", CraftingRecipeCategory.MISC, ItemStack.EMPTY, DefaultedList.of()) {
            @Override
            public boolean matches(CraftingInventory inventory, World world) {
                if (CarpetShadowSettings.shadowItemMode== CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return false;

                boolean enderchest = false;
                int count = 0;
                for(int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack2 = inventory.getStack(i);
                    if (!itemStack2.isEmpty()) {
                        if (itemStack2.getItem().equals(Items.ENDER_CHEST) && itemStack2.getCount() == 1)
                            enderchest = true;
                        count++;
                    }
                }
                return enderchest && count == 2;
            }

            @Override
            public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
                if (CarpetShadowSettings.shadowItemMode== CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return ItemStack.EMPTY;

                ItemStack item = null;
                ItemStack enderchest = null;
                for(int i = 0; i < inventory.size(); ++i) {
                    ItemStack itemStack2 = inventory.getStack(i);
                    if (!itemStack2.isEmpty()) {
                        if (itemStack2.getItem().equals(Items.ENDER_CHEST)  && itemStack2.getCount() == 1 && enderchest == null)
                            enderchest = itemStack2;
                        else
                            item = itemStack2;
                    }
                }
                if (item==null || enderchest==null)
                    return ItemStack.EMPTY;

                if (item.getItem().equals(Items.ENDER_CHEST) && item.getCount()==1){
                    item = enderchest;
                }
                String id = ((ShadowItem)(Object)item).getShadowId();
                if (id == null){
                    id = CarpetShadow.shadow_id_generator.nextString();
                }
                return Globals.getByIdOrAdd(id, item);
            }

            @Override
            public boolean fits(int width, int height) {
                if (CarpetShadowSettings.shadowItemMode== CarpetShadowSettings.Mode.UNLINK || !CarpetShadowSettings.shadowCraftingGeneration)
                    return false;
                return width * height >= 2;
            }

        };
        ((ImmutableMap.Builder)map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder())).put(identifier, recipe);
        builder.put(identifier, recipe);
    }
}