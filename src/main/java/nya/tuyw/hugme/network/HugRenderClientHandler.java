package nya.tuyw.hugme.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import nya.tuyw.hugme.HugMe;
import nya.tuyw.hugme.animation.AnimationManager;
import nya.tuyw.hugme.animation.HugAnimationEnum;
import nya.tuyw.hugme.events.RenderPlayerEventHandler;

import java.util.UUID;

public class HugRenderClientHandler {
    private static final Minecraft client = Minecraft.getInstance();

    public static void handleHugRender(String senderId, String receiverId, boolean isdone, int animationEnum, NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (client.level == null) return;
            AbstractClientPlayer sender = (AbstractClientPlayer) client.level.getPlayerByUUID(UUID.fromString(senderId));
            AbstractClientPlayer receiver = (AbstractClientPlayer) client.level.getPlayerByUUID(UUID.fromString(receiverId));
            if (sender == null || receiver == null) return;
            if (!isdone) {
                RenderPlayerEventHandler.lockPlayers(sender, receiver);
                startHugAnimation(sender, receiver, animationEnum);
            } else {
                RenderPlayerEventHandler.unlockPlayers(sender, receiver);
            }

        }).exceptionally(e -> {
            context.getNetworkManager().disconnect(Component.translatable("hugme.networking.failed", e.getMessage()));
            return null;
        });
    }

    private static void startHugAnimation(AbstractClientPlayer sender, AbstractClientPlayer receiver, int animationEnum) {
        HugAnimationEnum randomHugAnimationEnum = HugAnimationEnum.values()[animationEnum];
        HugMe.LOGGER.info("Starting hug animation " + randomHugAnimationEnum.toString() + " between " + sender.getName().getString() + " and " + receiver.getName().getString());
        AnimationManager.playHugAnimation(sender, receiver, randomHugAnimationEnum);
    }
}
