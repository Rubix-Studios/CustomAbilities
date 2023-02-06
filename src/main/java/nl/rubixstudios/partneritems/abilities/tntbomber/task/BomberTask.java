package nl.rubixstudios.partneritems.abilities.tntbomber.task;

import nl.rubixstudios.partneritems.abilities.tntbomber.TntBomberController;
import nl.rubixstudios.partneritems.abilities.tntbomber.data.BomberData;
import nl.rubixstudios.partneritems.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Djorr
 * @created 19/11/2022 - 14:02
 * @project PartnerItems
 */
public class BomberTask extends BukkitRunnable {

    private final TntBomberController tntBomberController;

    private final List<BomberData> bomberDataList;

    public BomberTask() {
        this.tntBomberController = TntBomberController.getInstance();

        this.bomberDataList = new ArrayList<>();
    }

    @Override
    public void run() {
        if (!this.tntBomberController.getBomberDatas().isEmpty()) {
            this.tntBomberController.getBomberDatas().forEach(bomberData -> {
                if (bomberData == null) return;

                bomberData.getEffectedBlocks().forEach(blockBombData -> {
                    if (blockBombData.isPlacedBack()) return;

                    if (System.currentTimeMillis() - blockBombData.getExplodeTime() >= Config.PARTNER_ITEM_TNT_BOMBER_TIME_BEFORE_PLACE_BACK * 1000L) {
                        final Block block = blockBombData.getLocation().getBlock();

                        block.setType(blockBombData.getMaterial());
                        block.setData(blockBombData.getData());

                        blockBombData.setPlacedBack(true);
                    }
                });

                if (isBomberDataClean(bomberData)) this.bomberDataList.add(bomberData);
            });

            this.bomberDataList.forEach(bomberData -> this.tntBomberController.getBomberDatas().remove(bomberData));
        }
    }

    private boolean isBomberDataClean(BomberData bomberData) {
        return bomberData.getEffectedBlocks().stream().filter(blockBombData -> !blockBombData.isPlacedBack()).count() == 0;
    }
}
