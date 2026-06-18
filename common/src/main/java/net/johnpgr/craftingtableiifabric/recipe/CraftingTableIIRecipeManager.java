package net.johnpgr.craftingtableiifabric.recipe;

import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingTableIIRecipeManager {
    private final CraftingTableIIScreenHandler screenHandler;
    private final LocalPlayer player;
    private final StackedContents recipeMatcher = new StackedContents();
    public List<RecipeCollection> results = List.of();

    public CraftingTableIIRecipeManager(CraftingTableIIScreenHandler handler, LocalPlayer localPlayer) {
        screenHandler = handler;
        player = localPlayer;
    }

    public void refreshInputs() {
        recipeMatcher.clear();
        player.getInventory().fillStackedContents(recipeMatcher);
        screenHandler.fillCraftSlotsStackedContents(recipeMatcher);
        refreshResults();
    }

    private void refreshResults() {
        var recipeBook = player.getRecipeBook();
        var allResults = recipeBook.getCollection(net.minecraft.client.RecipeBookCategories.CRAFTING_SEARCH);
        List<RecipeCollection> filtered = new ArrayList<>();

        for (RecipeCollection resultCollection : allResults) {
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
        }

        results = filtered;
    }

    @SuppressWarnings("resource")
    public RecipeResult firstResult(RecipeCollection collection) {
        Recipe<?> recipeEntry = collection.getRecipes(true).get(0);
        ItemStack itemStack = recipeEntry.getResultItem(player.level().registryAccess());
        return new RecipeResult(itemStack, recipeEntry);
    }


    public record RecipeResult(ItemStack stack, Recipe<?> recipe) {}
}
