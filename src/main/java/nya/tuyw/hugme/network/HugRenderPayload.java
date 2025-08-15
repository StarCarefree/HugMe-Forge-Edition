package nya.tuyw.hugme.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nya.tuyw.hugme.HugMe;

import java.util.function.Supplier;

public class HugRenderPayload {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HugMe.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                Packet.class,
                Packet::encode,
                Packet::decode,
                HugRenderPayload::handlePacket
        );
    }

    public static class Packet {
        private final String sender;
        private final String receiver;
        private final boolean isdone;
        private final int animationEnum;

        public Packet(String sender, String receiver, boolean isdone, int animationEnum) {
            this.sender = sender;
            this.receiver = receiver;
            this.isdone = isdone;
            this.animationEnum = animationEnum;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(sender);
            buf.writeUtf(receiver);
            buf.writeBoolean(isdone);
            buf.writeInt(animationEnum);
        }

        public static Packet decode(FriendlyByteBuf buf) {
            return new Packet(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readBoolean(),
                    buf.readInt()
            );
        }
    }

    public static void handlePacket(Packet packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> HugRenderClientHandler.handleHugRender(
                packet.sender,
                packet.receiver,
                packet.isdone,
                packet.animationEnum,
                ctx
        ));
        ctx.setPacketHandled(true);
    }
}