package ac.grim.grimac.checks.impl.autoblock;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

@CheckData(name = "AutoBlockA (Interact)",configName = "AutoBlock",setback = 1)
public class AutoBlockA extends Check implements PacketCheck {
    private int lastInteractEntity = -1;
    public AutoBlockA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.INTERACT)) {
                this.lastInteractEntity = wrapper.getEntityId();
            }

            //Even blocking due to a delay will send Interact before then
            if (wrapper.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                if (this.player.bukkitPlayer.isBlocking()) {
                    if (wrapper.getEntityId() != this.lastInteractEntity) {
                        flagAndAlert();
                        event.setCancelled(true);
                    } else {
                        reward();
                    }
                }
                this.lastInteractEntity = -1;
            }
        }
    }
}
