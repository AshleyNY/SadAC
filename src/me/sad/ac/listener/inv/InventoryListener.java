package me.sad.ac.listener.inv;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.SadAC;

public class InventoryListener implements Listener {

    private final SadAC plugin;

    public InventoryListener(SadAC plugin) {
        this.plugin = plugin;
    }

    /**
     * 监听玩家点击背包事件，检测异常物品
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = event.getPlayer();
        Item item = event.getHeldItem();

        // 跳过空气或空物品
        if (item == null || item.getId() == 0) {
            return;
        }

        // 检测1：物品数量超过最大堆叠数
        if (item.getCount() > item.getMaxStackSize()) {
            handleIllegalItem(player, item, "超量堆叠（最大堆叠数: " + item.getMaxStackSize() + "）");
            event.setCancelled(true);
            return;
        }

        // 检测2：非法物品ID（如负数或未注册的ID）
        if (item.getId() < 0 || !Item.get(item.getId()).isNull()) {
            handleIllegalItem(player, item, "非法物品ID: " + item.getId());
            event.setCancelled(true);
            return;
        }

        // 检测3：异常附魔（等级超过上限或非法附魔）
        for (Enchantment enchant : item.getEnchantments()) {
            if (enchant.getLevel() > enchant.getMaxLevel()) {
                handleIllegalItem(player, item, "异常附魔（" + enchant.getName() + " 等级: " + enchant.getLevel() + "，最大等级: " + enchant.getMaxLevel() + "）");
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * 处理非法物品：通知玩家、清空物品、记录日志
     */
    private void handleIllegalItem(Player player, Item item, String reason) {
        // 清空玩家手中的非法物品
        player.getInventory().setItemInHand(Item.get(0));
        // 记录控制台日志
        plugin.getLogger().info(TextFormat.YELLOW + "玩家 " + player.getName() + " 尝试持有异常物品: " + item.getName() + "，原因: " + reason);
    }
}

