package me.sad.ac.listener.combat;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.SadAC;
import me.sad.ac.listener.Reloadable;

public class CombatListener implements Listener, Reloadable {
    private final SadAC plugin;
    private boolean isCheckEnabled;  // 检测是否启用
    private double maxReachDistance; // 最大允许攻击距离

    public CombatListener(SadAC plugin) {
        this.plugin = plugin;
        reloadConfig(); // 初始化配置
    }
    @Override
    public void reloadConfig() {
        Config config = plugin.getCustomConfig();
        this.isCheckEnabled = config.getBoolean("checks.reach.enabled", true);
        this.maxReachDistance = config.getDouble("checks.reach.max-distance", 4); // 默认3.5格
    }
    /**
     * 重新加载配置（与命令的reload功能联动）
     */


    /**
     * 监听玩家攻击事件
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 仅处理玩家发起的攻击
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (!(attacker instanceof Player) || !(victim instanceof Player)) {
            return;
        }

        // 跳过创造模式玩家（可根据需求调整豁免条件）
        Player attackerPlayer = (Player) attacker;


        // 未启用检测时直接返回
        if (!isCheckEnabled) {
            return;
        }

        // 计算攻击距离
        double distance = attackerPlayer.distance(victim);
        if (distance > maxReachDistance) {
            event.setCancelled(true); // 取消攻击
        }
    }

    /**
     * 处理攻击距离违规
     */
}
