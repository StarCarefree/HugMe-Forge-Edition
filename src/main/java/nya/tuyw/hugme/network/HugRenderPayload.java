package nya.tuyw.hugme.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nya.tuyw.hugme.HugMe;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = HugMe.MODID)
public record HugRenderPayload(String sender, String receiver, boolean isdone, int animationEnum) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HugRenderPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("hugme", "render_hug"));

    public static final StreamCodec<FriendlyByteBuf, HugRenderPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            HugRenderPayload::sender,
            ByteBufCodecs.STRING_UTF8,
            HugRenderPayload::receiver,
            ByteBufCodecs.BOOL,
            HugRenderPayload::isdone,
            ByteBufCodecs.INT,
            HugRenderPayload::animationEnum,

            HugRenderPayload::new
    );

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                HugRenderPayload.TYPE,
                HugRenderPayload.STREAM_CODEC,
                HugRenderClientHandler::handleHugRender
        );
    }

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}