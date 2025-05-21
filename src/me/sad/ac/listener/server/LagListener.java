package me.sad.ac.listener.server;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Config;
import me.sad.ac.SadAC;
import me.sad.ac.listener.Reloadable;

import java.util.HashMap;
import java.util.Map;

public class LagListener implements Listener, Reloadable {

    private final SadAC plugin;
    private Map<String, Long> redstoneUpdateTimes = new HashMap<>();
    private int redstoneUpdateInterval;  // 改为非final，支持动态修改
    private int maxEntitiesPerChunk;     // 改为非final，支持动态修改

    public LagListener(SadAC plugin) {
        this.plugin = plugin;
        // 从配置文件加载参数（示例配置路径：checks.lag.redstone-update-interval）
        this.redstoneUpdateInterval = plugin.getCustomConfig().getInt("checks.lag.redstone-update-interval", 50);
        this.maxEntitiesPerChunk = plugin.getCustomConfig().getInt("checks.lag.max-entities-per-chunk", 50);
        // 启动实体数量定期检查任务（每30秒执行一次）
        new NukkitRunnable() {
            @Override
            public void run() {
                checkChunkEntities();
            }
        }.runTaskTimer(plugin, 0, 600);
    }
@Override
    // 新增：动态重载配置方法
    public void reloadConfig() {
        Config config = plugin.getCustomConfig();
        this.redstoneUpdateInterval = config.getInt("checks.lag.redstone-update-interval", 50);
        this.maxEntitiesPerChunk = config.getInt("checks.lag.max-entities-per-chunk", 50);
    }

    /**
     * 监听方块更新事件，检测高频红石
     */
    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent event) {
        Block block = event.getBlock();
        // 扩展检测类型：新增红石线（BlockRedstoneWire）
        if (!(block instanceof BlockRedstoneTorch
            || block instanceof BlockRedstoneComparator
            || block instanceof BlockRedstoneRepeaterPowered
            || block instanceof BlockRedstoneRepeaterUnpowered
            || block instanceof BlockRedstoneWire)) {  // 新增
            return;
        }

        String blockKey = getBlockKey(block);
        long currentTime = System.currentTimeMillis();
        Long lastUpdate = redstoneUpdateTimes.get(blockKey);

        // 首次更新或间隔超过阈值，记录时间
        if (lastUpdate == null || currentTime - lastUpdate > redstoneUpdateInterval) {
            redstoneUpdateTimes.put(blockKey, currentTime);
            return;
        }

        // 高频更新，删除方块并记录日志
        block.getLevel().setBlock(block, Block.get(Block.AIR));
        plugin.getLogger().info("检测到高频红石，位置: " + block.getX() + "," + block.getY() + "," + block.getZ());
    }

    /**
     * 定期检查区块内实体数量（经验球、船等）
     */
    private void checkChunkEntities() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            Level level = player.getLevel();
            int playerChunkX = player.getChunkX();
            int playerChunkZ = player.getChunkZ();

            // 检查玩家附近10个区块（可配置）
            for (int x = playerChunkX - 10; x <= playerChunkX + 10; x++) {
                for (int z = playerChunkZ - 10; z <= playerChunkZ + 10; z++) {
                    FullChunk chunk = level.getChunk(x, z, false);
                    if (chunk == null) continue;

                    Map<Long, Entity> entities = chunk.getEntities();
                    int targetEntityCount = 0;

                    // 统计目标实体数量（经验球、船）
                    for (Entity entity : entities.values()) {
                        if (entity instanceof EntityXPOrb || entity instanceof EntityBoat) {
                            targetEntityCount++;
                        }
                    }

                    // 超过阈值则清理多余实体
                    if (targetEntityCount > maxEntitiesPerChunk) {
                        int removeCount = targetEntityCount - maxEntitiesPerChunk;
                        int removed = 0;
                        for (Entity entity : entities.values()) {
                            if ((entity instanceof EntityXPOrb || entity instanceof EntityBoat) && removed < removeCount) {
                                entity.close();
                                removed++;
                            }
                        }
                        plugin.getLogger().info("区块 " + chunk.getX() + "," + chunk.getZ() + " 实体过多，清理 " + removed + " 个实体");
                    }
                }
            }
        }
    }

    /**
     * 生成方块位置的唯一标识
     */
    private String getBlockKey(Block block) {
        return block.getLevel().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
    }
}
