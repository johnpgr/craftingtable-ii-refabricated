package net.johnpgr.craftingtableiifabric.recipe;

import net.johnpgr.craftingtableiifabric.screen.CraftingTableIIScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class CraftingTableIIRecipeManager {
    private final CraftingTableIIScreenHandler screenHandler;
    private final LocalPlayer player;
    private final StackedContents recipeMatcher = new StackedContents();
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

    public static RecipeResult firstResult(RecipeCollection collection) {
        RecipeHolder<?> recipeEntry = collection.getRecipes(true).getFirst();
        ItemStack itemStack = recipeEntry.value().getResultItem(Minecraft.getInstance().level.registryAccess());
        return new RecipeResult(itemStack, recipeEntry);
    }

    public record RecipeResult(ItemStack stack, RecipeHolder<?> recipe) {
    }
}
