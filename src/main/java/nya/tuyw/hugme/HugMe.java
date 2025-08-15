package nya.tuyw.hugme;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import nya.tuyw.hugme.command.HugCommandHandler;
import nya.tuyw.hugme.item.HugTicketItem;
import nya.tuyw.hugme.network.HugRenderPayload;
import org.slf4j.Logger;

import java.io.File;

@Mod(HugMe.MODID)
public class HugMe {
    public static final String MODID = "hugme";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> HUGME_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HugMe.MODID);
    public static final RegistryObject<Item> HUG_TICKET = HUGME_ITEMS.register("hug_ticket", () -> new HugTicketItem(new Item.Properties().stacksTo(64)));

    public HugMe(FMLJavaModLoadingContext context) {
        HUGME_ITEMS.register(context.getModEventBus());
        context.getModEventBus().addListener(this::addCreative);
        HugRenderPayload.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(HUG_TICKET);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        HugCommandHandler.register(event.getServer().getCommands().getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (ServerLifecycleHooks.getCurrentServer() == null) return;
            File playerDataFile = new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), player.getUUID() + ".dat");
            if (!playerDataFile.exists()) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("hugme","hug_ticket")),16);
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false);
                }
            }
        }
    }
}
