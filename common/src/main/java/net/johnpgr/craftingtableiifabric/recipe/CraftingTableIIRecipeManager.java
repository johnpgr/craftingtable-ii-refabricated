package net.johnpgr.craftingtableiifabric.recipe;

import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.client.Minecraft;
//? if >=1.21.3 {
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
//? }
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
//? if <1.21.3 {
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.client.RecipeBookCategories;
//? } else {
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
//? }
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class CraftingTableIIRecipeManager {
    private final CraftingTableIIScreenHandler screenHandler;
    private final LocalPlayer player;
    //? if <1.21.3 {
    private final StackedContents recipeMatcher = new StackedContents();
    //? } else {
    private static final ContextMap EMPTY_DISPLAY_CONTEXT = new ContextMap.Builder().create(new ContextKeySet.Builder().build());
    private final StackedItemContents recipeMatcher = new StackedItemContents();
    //? }
    public List<RecipeCollection> results = List.of();

    public CraftingTableIIRecipeManager(CraftingTableIIScreenHandler screenHandler, LocalPlayer player) {
        this.screenHandler = screenHandler;
        this.player = player;
    }

    public void refreshInputs() {
        recipeMatcher.clear();
        player.getInventory().fillStackedContents(recipeMatcher);
        screenHandler.fillCraftSlotsStackedContents(recipeMatcher);
        refreshResults();
    }

    private void refreshResults() {
        var recipeBook = player.getRecipeBook();
        //? if <1.21.3 {
        var allResults = recipeBook.getCollection(RecipeBookCategories.CRAFTING_SEARCH);
        //? } else {
        var allResults = recipeBook.getCollection(SearchRecipeBookCategory.CRAFTING);
        //? }
        List<RecipeCollection> filtered = new ArrayList<>();

        for (RecipeCollection resultCollection : allResults) {
            //? if <1.21.3 {
            resultCollection.updateKnownRecipes(recipeBook);
            resultCollection.canCraft(
                    recipeMatcher,
                    screenHandler.getGridWidth(),
                    screenHandler.getGridHeight(),
                    recipeBook
            );
            if (resultCollection.hasKnownRecipes() && resultCollection.hasFitting() && resultCollection.hasCraftable()) {
                filtered.add(resultCollection);
            }
            //? } else {
            resultCollection.selectRecipes(recipeMatcher, display -> true);
            if (resultCollection.hasAnySelected() && resultCollection.hasCraftable()) {
                filtered.add(resultCollection);
            }
            //? }
        }

        results = filtered;
    }

    public static RecipeResult firstResult(RecipeCollection collection) {
        //? if <1.21.3 {
        RecipeHolder<?> recipeEntry = collection.getRecipes(true).getFirst();
        ItemStack itemStack = recipeEntry.value().getResultItem(Minecraft.getInstance().level.registryAccess());
        //? } else {
        RecipeDisplayEntry displayEntry = collection.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE).getFirst();
        ItemStack itemStack = displayResult(displayEntry);
        RecipeDisplayId recipeEntry = displayEntry.id();
        //? }
        return new RecipeResult(itemStack, recipeEntry);
    }

    //? if >=1.21.3 {
    public static ItemStack displayResult(RecipeDisplayEntry displayEntry) {
        var results = displayEntry.resultItems(EMPTY_DISPLAY_CONTEXT);
        return results.isEmpty() ? ItemStack.EMPTY : results.getFirst();
    }

    //? }

    //? if <1.21.3 {
    public record RecipeResult(ItemStack stack, RecipeHolder<?> recipe) {
    }
    //? } else {
    public record RecipeResult(ItemStack stack, RecipeDisplayId recipe) {
    }
    //? }
}
