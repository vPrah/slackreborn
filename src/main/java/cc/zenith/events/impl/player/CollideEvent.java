package cc.zenith.events.impl.player;

import cc.zenith.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;

@Getter
@Setter
@AllArgsConstructor
public class CollideEvent extends Event {
    public Block block;
    public AxisAlignedBB boundingBox;
    public double x, y, z;
}