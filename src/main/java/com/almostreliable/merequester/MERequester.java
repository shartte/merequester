package com.almostreliable.merequester;

import com.almostreliable.merequester.data.MERequesterData;
import com.almostreliable.merequester.network.PacketHandler;
import com.almostreliable.merequester.requester.RequesterMenu;
import com.almostreliable.merequester.terminal.RequesterTerminalMenu;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

@Mod(BuildConfig.MOD_ID)
public final class MERequester {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String REQUESTER_ID = "requester";
    public static final String TERMINAL_ID = "requester_terminal";

    public MERequester(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(Registration::registerContents);
        modEventBus.addListener(Registration::registerCapabilities);
        modEventBus.addListener(Registration.Tab::initContents);
        modEventBus.addListener(PacketHandler::onPacketRegistration);
        MERequesterData.DR.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }
}
