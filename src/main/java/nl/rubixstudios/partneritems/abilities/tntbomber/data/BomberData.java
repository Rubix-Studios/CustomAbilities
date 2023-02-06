package nl.rubixstudios.partneritems.abilities.tntbomber.data;

import javafx.scene.paint.Material;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Djorr
 * @created 19/11/2022 - 14:03
 * @project PartnerItems
 */

@Getter
@Setter
public class BomberData {

    private List<BlockBombData> effectedBlocks;

    public BomberData() {
        this.effectedBlocks = new ArrayList<>();
    }

    public void addNewBlockData(Block block, double seconds) {
        final BlockBombData blockBombData = this.getBlockBombData(block, seconds);
        this.effectedBlocks.add(blockBombData);
    }

    private BlockBombData getBlockBombData(Block block, double seconds) {
        return new BlockBombData(block.getLocation(), block.getType(), block.getData(), false, (long) (System.currentTimeMillis() + (seconds * 1000L)));
    }

    public void placeAllBlocksBack() {
        this.effectedBlocks.forEach(blockBombData -> {
            final Block block = blockBombData.getLocation().getBlock();

            block.setType(blockBombData.getMaterial());
            block.setData(blockBombData.getData());
        });
    }


}
