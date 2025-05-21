package me.sad.ac.listener.inv;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.SadAC;
import me.sad.ac.listener.Reloadable;

import java.util.HashSet;
import java.util.Set;

public class InventoryListener implements Listener , Reloadable {


    private final SadAC plugin;

    Set<Integer> bannedItems ;
    public InventoryListener(SadAC plugin) {
        this.plugin = plugin;
        this.bannedItems  = plugin.getBannedItemIds();
    }

    @Override
    public void reloadConfig() {
        this.bannedItems  = plugin.getBannedItemIds();
        plugin.getLogger().info("当前违禁物品列表: " + bannedItems);
    }
    /**
     * 监听玩家点击背包事件，检测异常物品
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = event.getPlayer();
        int slot = event.getSlot();
        Item sourceItem = event.getSourceItem(); // 获取点击位置的物品

        // 改为检测源物品而不是手持物品
        if (sourceItem == null || sourceItem.getId() == 0) {
            return;
        }

        // 调试信息显示实际操作的物品

        if (bannedItems.contains(sourceItem.getId())) {
            // 清除具体槽位的物品
            player.getInventory().setItem(slot, Item.get(0));
            event.setCancelled(true);
        }


    }
    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        checkIllegalItem(event.getPlayer(), event.getItem(), event);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        checkIllegalItem(event.getPlayer(), event.getItem(), event);
    }

    private void checkIllegalItem(Player player, Item item, Cancellable event) {
        if (item == null || item.getId() == 0) return;

        Set<Integer> bannedItems = plugin.getBannedItemIds();
        if (bannedItems.contains(item.getId())) {
            handleIllegalItem(player, item, "使用违禁物品");
            event.setCancelled(true);
            // 立即从背包移除
            player.getInventory().remove(item);
        }
    }
    private void handleIllegalItem(Player player, Item item, String reason) {
        plugin.getLogger().info(TextFormat.YELLOW + "玩家 " + player.getName() + " 尝试持有异常物品: " + item.getName() + "，原因: " + reason);
    }
}

