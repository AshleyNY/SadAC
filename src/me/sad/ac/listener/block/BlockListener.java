package me.sad.ac.listener.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.SadAC;

import java.util.HashMap;
import java.util.Map;

// 实现 Listener 接口，表明这是一个事件监听器类
public class BlockListener implements Listener {

    private final SadAC plugin;
    // 存储玩家开始破坏方块的时间，键为玩家和方块组合
    private final Map<Player, Map<Block, Long>> playerBlockBreakStartTime = new HashMap<>();
    // 方块破坏最短时间阈值（毫秒）
    private long minBreakTime;
    // 标记检查是否启用
    private boolean isCheckEnabled;

    public BlockListener(SadAC plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        Config config = plugin.getCustomConfig();
        this.isCheckEnabled = config.getBoolean("checks.instant-break.enabled", true);
        this.minBreakTime = getMinBreakTimeFromConfig(config);
        }

    /**
     * 从配置文件中获取方块破坏最短时间阈值
     * @param config 配置对象
     * @return 方块破坏最短时间阈值
     */
    private long getMinBreakTimeFromConfig(Config config) {
        try {
            return config.getLong("checks.instant-break.min-break-time", 500);
        } catch (Exception e) {
            // 记录错误日志并使用默认值
            plugin.getLogger().info("Failed to load min-break-time from configuration: " + e.getMessage());
            return 500;
        }
    }

    // 监听玩家点击方块事件，记录开始破坏时间
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && isCheckEnabled) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            if (isPlayerExempt(player)) {
                return;
            }
            playerBlockBreakStartTime.computeIfAbsent(player, k -> new HashMap<>()).put(block, System.currentTimeMillis());
        }
    }

    // 监听玩家破坏方块事件
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isCheckEnabled) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // 新增：免除地狱岩检测（地狱岩ID通常为87，具体以Nukkit版本为准）
        if (block.getId() == 87) {
            return;
        }
        if(!block.isNormalBlock()){
            return;
        }
        // 检查玩家是否豁免检查（如创造模式）
        if (isPlayerExempt(player)) {
            return;
        }

        checkInstantBreak(event, player, block);
    }

    /**
     * 检查是否存在方块瞬间破坏行为
     * @param event 方块破坏事件
     * @param player 玩家对象
     * @param block 被破坏的方块
     */
    private void checkInstantBreak(BlockBreakEvent event, Player player, Block block) {
        Map<Block, Long> playerBlockTimes = playerBlockBreakStartTime.get(player);
        if (playerBlockTimes == null) {
            handleViolation(event, player, 0);
            return;
        }

        Long startTime = playerBlockTimes.get(block);
        if (startTime == null) {
            handleViolation(event, player, 0);
            return;
        }

        long currentTime = System.currentTimeMillis();
        // 计算方块破坏时间
        long breakTime = currentTime - startTime;

        // 检查破坏时间是否小于阈值
        if (breakTime < minBreakTime) {
            handleViolation(event, player, breakTime);
        } else {
            // 移除该方块的记录
            playerBlockTimes.remove(block);
            if (playerBlockTimes.isEmpty()) {
                playerBlockBreakStartTime.remove(player);
            }
        }
    }

    /**
     * 发送调试信息给玩家
     * @param player 玩家对象
     * @param breakTime 玩家实际破坏方块的时间
     */


    /**
     * 检查玩家是否豁免检查
     * @param player 玩家对象
     * @return 如果豁免则返回 true，否则返回 false
     */
    private boolean isPlayerExempt(Player player) {
        // 示例：创造模式玩家豁免检查
        return player.getGamemode() == 1;
    }

    /**
     * 处理玩家违规行为
     * @param event 方块破坏事件
     * @param player 玩家对象
     * @param breakTime 玩家实际破坏方块的时间
     */
    private void handleViolation(BlockBreakEvent event, Player player, long breakTime) {
        event.setCancelled(true);
        }
}