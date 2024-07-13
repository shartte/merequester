package com.almostreliable.merequester;

import appeng.api.AECapabilities;
import appeng.api.parts.PartModels;
import appeng.block.AEBaseBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.almostreliable.merequester.requester.RequesterBlock;
import com.almostreliable.merequester.requester.RequesterBlockEntity;
import com.almostreliable.merequester.terminal.RequesterTerminalPart;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

import static com.almostreliable.merequester.MERequester.REQUESTER_ID;
import static com.almostreliable.merequester.MERequester.TERMINAL_ID;

public final class Registration {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BuildConfig.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BuildConfig.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE,
        BuildConfig.MOD_ID
    );

    public static final DeferredBlock<RequesterBlock> REQUESTER_BLOCK = BLOCKS.registerBlock(
        REQUESTER_ID,
        RequesterBlock::new,
        AEBaseBlock.metalProps()
    );
    public static final DeferredItem<BlockItem> REQUESTER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        REQUESTER_ID,
        REQUESTER_BLOCK,
        new Item.Properties()
    );
    public static final DeferredItem<PartItem<RequesterTerminalPart>> REQUESTER_TERMINAL = ITEMS.registerItem(
        TERMINAL_ID,
        properties -> {
            PartModels.registerModels(PartModelsHelper.createModels(RequesterTerminalPart.class));

            return new PartItem<>(
                properties,
                RequesterTerminalPart.class,
                RequesterTerminalPart::new
            );
        }
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RequesterBlockEntity>> REQUESTER_ENTITY
        = BLOCK_ENTITY_TYPES.register(REQUESTER_ID, () -> {
        var type = BlockEntityType.Builder
            .of(RequesterBlockEntity::new, REQUESTER_BLOCK.get())
            .build(null);
        AEBaseBlockEntity.registerBlockEntityItem(type, REQUESTER_BLOCK.asItem());
        REQUESTER_BLOCK.get().setBlockEntity(RequesterBlockEntity.class, type, null, null);
        return type;
    });

    private Registration() {}

    static void registerContents(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
            Tab.registerTab(event);
        }
    }

    static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            AECapabilities.IN_WORLD_GRID_NODE_HOST,
            REQUESTER_ENTITY.get(),
            (requester, ctx) -> requester
        );
    }

    public static final class Tab {

        public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Utils.getRL("tab"));
        private static final CreativeModeTab TAB = CreativeModeTab.builder()
            .title(Utils.translate("itemGroup", "tab"))
            .icon(REQUESTER_BLOCK::toStack)
            .noScrollBar()
            .build();

        private Tab() {}

        static void initContents(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == TAB_KEY) {
                event.accept(REQUESTER_BLOCK);
                event.accept(REQUESTER_TERMINAL);
            }
        }

        private static void registerTab(RegisterEvent registerEvent) {
            registerEvent.register(Registries.CREATIVE_MODE_TAB, TAB_KEY.location(), () -> TAB);
        }
    }
}
