package net.johnpgr.craftingtableiifabric.entity;

import net.johnpgr.craftingtableiifabric.CraftingTableII;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CraftingTableIIEntityModel {
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 64;

    public static final ModelLayerLocation TABLE_MODEL_LAYER = layer("table");
    public static final ModelLayerLocation DOOR_MODEL_LAYER = layer("door");
    public static final ModelLayerLocation DOOR_SIDE_MODEL_LAYER = layer("door_side");
    public static final ModelLayerLocation DOOR_SIDE1_MODEL_LAYER = layer("door_side1");
    public static final ModelLayerLocation DOOR_TOP_SIDE_MODEL_LAYER = layer("door_top_sider");
    public static final ModelLayerLocation DOOR_TOP_SIDE1_MODEL_LAYER = layer("door_top_side1");
    public static final ModelLayerLocation BOOK_MODEL_LAYER = layer("book");

    private CraftingTableIIEntityModel() {
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(CraftingTableII.id("craftingtableii"), name);
    }

    public static Map<ModelLayerLocation, Supplier<LayerDefinition>> getLayerDefinitions() {
        Map<ModelLayerLocation, Supplier<LayerDefinition>> map = new LinkedHashMap<>();
        map.put(TABLE_MODEL_LAYER, CraftingTableIIEntityModel::setupTable);
        map.put(DOOR_MODEL_LAYER, CraftingTableIIEntityModel::setupDoor);
        map.put(DOOR_SIDE_MODEL_LAYER, CraftingTableIIEntityModel::setupDoorSide);
        map.put(DOOR_SIDE1_MODEL_LAYER, CraftingTableIIEntityModel::setupDoorSide1);
        map.put(DOOR_TOP_SIDE_MODEL_LAYER, CraftingTableIIEntityModel::setupDoorTopSide);
        map.put(DOOR_TOP_SIDE1_MODEL_LAYER, CraftingTableIIEntityModel::setupDoorTopSide1);
        map.put(BOOK_MODEL_LAYER, CraftingTableIIEntityModel::setupBook);
        return map;
    }

    private static LayerDefinition setupTable() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "table",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7f, 0f, -7f, 9f, 16f, 14f),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupDoor() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "door",
                CubeListBuilder.create().texOffs(96, 0).addBox(0f, 0f, 0f, 1f, 16f, 13f),
                PartPose.offsetAndRotation(6f, 0f, -6f, 0f, 0f, 0f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupDoorSide() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "door_side",
                CubeListBuilder.create().texOffs(61, 0).addBox(0f, 0f, 0f, 4f, 16f, 1f),
                PartPose.offset(2f, 0f, -7f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupDoorSide1() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "door_side1",
                CubeListBuilder.create().texOffs(71, 0).addBox(0f, 0f, 0f, 4f, 16f, 1f),
                PartPose.offset(2f, 0f, 6f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupDoorTopSide() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "door_top_side",
                CubeListBuilder.create().texOffs(0, 46).addBox(0f, 0f, 0f, 4f, 1f, 12f),
                PartPose.offset(2f, 0f, -6f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupDoorTopSide1() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "door_top_side1",
                CubeListBuilder.create().texOffs(0, 33).addBox(0f, 0f, 0f, 4f, 1f, 12f),
                PartPose.offset(2f, 15f, -6f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static LayerDefinition setupBook() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "book",
                CubeListBuilder.create().texOffs(61, 23).addBox(0f, 0f, 0f, 5f, 1f, 3f),
                PartPose.offsetAndRotation(-2f, -1f, 3.5f, 0f, 0.4833219f, 0f)
        );
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
