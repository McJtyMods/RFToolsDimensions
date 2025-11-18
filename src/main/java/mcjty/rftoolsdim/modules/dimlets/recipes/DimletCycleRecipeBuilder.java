package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DimletCycleRecipeBuilder implements IRecipeBuilder<DimletCycleRecipeBuilder> {
    private final Item result;
    private final int count;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private String input;
    private String output;
    private boolean hasCriteria = false;

    public DimletCycleRecipeBuilder(ItemLike resultIn, int countIn) {
        this.result = resultIn.asItem();
        this.count = countIn;
    }

    public static DimletCycleRecipeBuilder shapedRecipe(ItemLike resultIn) {
        return shapedRecipe(resultIn, 1);
    }

    public static DimletCycleRecipeBuilder shapedRecipe(ItemLike resultIn, int countIn) {
        return new DimletCycleRecipeBuilder(resultIn, countIn);
    }

    @Override
    public DimletCycleRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
        return this.define(symbol, Ingredient.of(tagIn));
    }

    public DimletCycleRecipeBuilder input(String input) {
        this.input = input;
        return this;
    }

    public DimletCycleRecipeBuilder output(String output) {
        this.output = output;
        return this;
    }

    @Override
    public DimletCycleRecipeBuilder define(Character symbol, ItemLike itemIn) {
        return this.define(symbol, Ingredient.of(itemIn));
    }

    @Override
    public DimletCycleRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    @Override
    public DimletCycleRecipeBuilder patternLine(String patternIn) {
        if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternIn);
            return this;
        }
    }

    public DimletCycleRecipeBuilder addCriterion(String name, Criterion criterionIn) {
        this.advancementBuilder.addCriterion(name, criterionIn);
        this.hasCriteria = true;
        return this;
    }

    @Override
    public DimletCycleRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    @Override
    public void build(RecipeOutput consumerIn) {
        this.build(consumerIn, BuiltInRegistries.ITEM.getKey(this.result));
    }

    @Override
    public void build(RecipeOutput consumerIn, String save) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.result);
        if ((ResourceLocation.parse(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, ResourceLocation.parse(save));
        }
    }

    @Override
    public void build(RecipeOutput consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(ResourceLocation.parse("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        String folder = "dimlets";
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + folder + "/" + id.getPath());
        AdvancementHolder advancement = this.advancementBuilder.build(advancementId);

        ShapedRecipePattern shapedPattern = ShapedRecipePattern.of(this.key, this.pattern);
        ItemStack resultStack = new ItemStack(this.result, this.count);
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.group == null ? "" : this.group, CraftingBookCategory.MISC, shapedPattern, resultStack);
        DigitCycleRecipe recipe = new DigitCycleRecipe(shapedRecipe, input, output);
        consumerIn.accept(id, recipe, advancement);
    }

    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for(String s : this.pattern) {
                for(int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (!hasCriteria) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

}
