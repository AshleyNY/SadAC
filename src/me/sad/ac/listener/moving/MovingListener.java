package me.sad.ac.listener.moving;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.utils.Config;
import me.sad.ac.SadAC;
import me.sad.ac.listener.Reloadable;

import java.util.HashMap;
import java.util.Map;

// 实现 Listener 接口，表明这是一个事件监听器类
public class MovingListener implements Listener, Reloadable {

    private final SadAC plugin;
    // 用于存储每个玩家的累计移动距离
    private final Map<Player, Double> playerDistanceMap = new HashMap<>();
    // 用于标记玩家是否刚被拉回
    private final Map<Player, Boolean> playerWasPulledBack = new HashMap<>();
    private boolean isCheckSpeed;
    private boolean isCheckFlying;
    private boolean isCheckWallhack;
    private double maxRunSpeed;
    private double maxFlyingSpeed;
    private int Steps;
    // 构造函数，传入插件主类实例
    public MovingListener(SadAC plugin) {
        this.plugin = plugin;
        reloadConfig();
    }
    @Override
    public void reloadConfig() {
        Config config = plugin.getCustomConfig();
        isCheckFlying = config.getBoolean("checks.flight.enabled");
        isCheckSpeed = config.getBoolean("checks.flight.speed");
        isCheckWallhack = config.getBoolean("checks.wallhack.enabled");
        maxRunSpeed = config.getDouble("checks.speed.max-speed");
        maxFlyingSpeed = config.getDouble("checks.flight.max-speed");
        Steps = config.getInt("checks.wallhack.steps");
    }
    // 监听玩家移动事件
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // 如果玩家刚被拉回，跳过此次移动距离计算
        if (playerWasPulledBack.getOrDefault(player, false)) {
            playerWasPulledBack.put(player, false);
            return;
        }
        checkFlight(player, event);
        checkSpeed(player, event);
        checkWallHack(player, event);
    }

    private void checkFlight(Player player, PlayerMoveEvent event) {
        if (isCheckFlying) {
            double maxSpeed = maxFlyingSpeed;
            double currentDistance = event.getFrom().distance(event.getTo());
            double totalDistance = playerDistanceMap.getOrDefault(player, 0.0) + currentDistance;

            if (!player.isOnGround() && totalDistance > maxSpeed) {
                player.teleport(event.getFrom());
                // 标记玩家刚被拉回
                playerWasPulledBack.put(player, true);
                // 重置玩家的累计移动距离
                playerDistanceMap.put(player, 0.0);
            } else {
                // 更新玩家的累计移动距离
                playerDistanceMap.put(player, totalDistance);
            }
        }
    }

    private void checkSpeed(Player player, PlayerMoveEvent event) {
        if (isCheckSpeed) {
            double maxSpeed = maxRunSpeed;
            double currentDistance = event.getFrom().distance(event.getTo());
            double totalDistance = playerDistanceMap.getOrDefault(player, 0.0) + currentDistance;

            if (player.isOnGround() && totalDistance > maxSpeed) {
                player.teleport(event.getFrom());
                // 标记玩家刚被拉回
                playerWasPulledBack.put(player, true);
                // 重置玩家的累计移动距离
                playerDistanceMap.put(player, 0.0);
            } else {
                // 更新玩家的累计移动距离
                playerDistanceMap.put(player, totalDistance);
            }
        }
    }
    private void checkWallHack(Player player, PlayerMoveEvent event) {
        // 豁免创造模式玩家（可选）
        if (player.getGamemode() == 1) {
            return;
        }
        if(!isCheckWallhack)
        {
            return;
        }   // 获取移动起点和终点坐标
        double fromX = event.getFrom().x;
        double fromY = event.getFrom().y;
        double fromZ = event.getFrom().z;
        double toX = event.getTo().x;
        double toY = event.getTo().y;
        double toZ = event.getTo().z;

        // 采样路径上的10个点（可调整步数）
        int steps = Steps;
        for (int i = 0; i <= steps; i++) {
            // 计算当前采样点坐标
            double x = fromX + (toX - fromX) * (i / (double) steps);
            double y = fromY + (toY - fromY) * (i / (double) steps);
            double z = fromZ + (toZ - fromZ) * (i / (double) steps);

            // 获取当前点的方块
            Block block = player.getLevel().getBlock((int) x, (int) y, (int) z);

            // 检查是否是实体方块（如石头、砖块）
            if (block.isNormalBlock()) {
                // 检测到穿墙，拉回原位
                player.teleport(event.getFrom());
                return;
            }
        }
    }
}
