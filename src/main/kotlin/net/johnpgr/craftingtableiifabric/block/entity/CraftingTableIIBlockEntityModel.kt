package net.johnpgr.craftingtableiifabric.block.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.johnpgr.craftingtableiifabric.CraftingTableIIMod
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.LoadedEntityModels

@Environment(EnvType.CLIENT)
object CraftingTableIIBlockEntityModel {
    private const val TEXTURE_WIDTH = 128
    private const val TEXTURE_HEIGHT = 64

    fun register() {
        getEntries()
            .forEach { (entityLayer, textureModelData) ->
                EntityModelLayerRegistry.registerModelLayer(entityLayer) { textureModelData }
            }
    }


    val tableModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "table"
    )
    val doorModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "door"
    )
    val doorSideModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "door_side"
    )
    val doorSide1ModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "door_side1"
    )
    val doorTopSideModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "door_top_sider"
    )
    val doorTopSide1ModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "door_top_side1"
    )
    val bookModelLayer = EntityModelLayer(
        CraftingTableIIMod.id("craftingtableii"),
        "book"
    )

    private fun setupTable(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "table",
            ModelPartBuilder
                .create()
                .uv(0, 0)
                .cuboid(-7f, 0f, -7f, 9f, 16f, 14f)
                .mirrored(true),
            ModelTransform.NONE
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoor(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door",
            ModelPartBuilder
                .create()
                .uv(96, 0)
                .cuboid(0f, 0f, 0f, 1f, 16f, 13f)
                .mirrored(true),
            ModelTransform.of(6f, 0f, -6f, 0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorSide(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_side",
            ModelPartBuilder
                .create()
                .uv(61, 0)
                .cuboid(0f, 0f, 0f, 4f, 16f, 1f)
                .mirrored(true),
            ModelTransform.of(2f, 0f, -7f, 0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorSide1(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_side1",
            ModelPartBuilder
                .create()
                .uv(71, 0)
                .cuboid(0f, 0f, 0f, 4f, 16f, 1f)
                .mirrored(true),
            ModelTransform.of(2f, 0f, 6f, 0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorTopSide(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_top_side",
            ModelPartBuilder
                .create()
                .uv(0, 46)
                .cuboid(0f, 0f, 0f, 4f, 1f, 12f)
                .mirrored(true),
            ModelTransform.of(2f, 0f, -6f, 0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupDoorTopSide1(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "door_top_side1",
            ModelPartBuilder
                .create()
                .uv(0, 33)
                .cuboid(0f, 0f, 0f, 4f, 1f, 12f)
                .mirrored(true),
            ModelTransform.of(2f, 15f, -6f, 0f, 0f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun setupBook(): TexturedModelData {
        val md = ModelData()
        val md1 = md.root
        md1.addChild(
            "book",
            ModelPartBuilder
                .create()
                .uv(61, 23)
                .cuboid(0f, 0f, 0f, 5f, 1f, 3f)
                .mirrored(true),
            ModelTransform.of(-2f, -1f, 3.5f, 0f, 0.4833219f, 0f)
        )
        return TexturedModelData.of(md, TEXTURE_WIDTH, TEXTURE_HEIGHT)
    }

    private fun getEntries(): Map<EntityModelLayer, TexturedModelData> {
        val map = mutableMapOf<EntityModelLayer, TexturedModelData>()

        map[tableModelLayer] = setupTable()
        map[doorModelLayer] = setupDoor()
        map[doorSideModelLayer] = setupDoorSide()
        map[doorSide1ModelLayer] = setupDoorSide1()
        map[doorTopSideModelLayer] = setupDoorTopSide()
        map[doorTopSide1ModelLayer] = setupDoorTopSide1()
        map[bookModelLayer] = setupBook()

        return map
    }

    val loadedEntityModels: LoadedEntityModels = LoadedEntityModels(getEntries())
}
