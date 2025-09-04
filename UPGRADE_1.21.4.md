# CraftingTable II Refabricated - 1.21.4 Upgrade

This branch contains the 1.21.4 upgrade for CraftingTable II Refabricated.

## Key Changes Made

### API Updates
- Replaced `BuiltinItemRendererRegistry.DynamicItemRenderer` with `SimpleSpecialModelRenderer`
- Updated Minecraft version to 1.21.4
- Updated Fabric API to 0.115.0+1.21.4
- Updated Java version requirement to 21

### Dependencies Updated
- `minecraft_version=1.21.4`
- `yarn_mappings=1.21.4+build.8`
- `loader_version=0.16.10`
- `fabric_version=0.115.0+1.21.4`

### Breaking Changes
The `BuiltinItemRendererRegistry.DynamicItemRenderer` API was removed in 1.21.4+. The new implementation uses `SimpleSpecialModelRenderer` which integrates with Minecraft's data-driven model system.

## Building
Make sure you have Java 21 installed and run:
```bash
./gradlew build
```

## References
- [ProductiveSlimes-Fabric 1.21.4 implementation](https://github.com/ChesyDev/ProductiveSlimes-Fabric/tree/1.21.4)
- [Fabric 1.21.4 migration guide](https://fabricmc.net/develop)