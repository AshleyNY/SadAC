package me.sad.ac;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.command.SadBaseCommand;
import me.sad.ac.command.SadCommand;
import me.sad.ac.listener.Reloadable;
import me.sad.ac.listener.block.BlockListener;
import me.sad.ac.listener.combat.CombatListener;
import me.sad.ac.listener.moving.MovingListener;
import me.sad.ac.listener.inv.InventoryListener;
import me.sad.ac.listener.server.LagListener;

import java.io.File;
import java.util.*;

public class SadAC extends PluginBase {

    private Config config;
    private Config BannedItemConfig;
    // 添加监听器引用

    private final Set<Integer> bannedItemIds = new HashSet<>();
    private final List<Reloadable> reloadableListeners = new ArrayList<>();

    public void registerReloadableListener(Reloadable listener) {
        getServer().getPluginManager().registerEvents((Listener) listener, this);  // 假设监听器都实现了 Listener 接口
        reloadableListeners.add(listener);
    }// 新增：集中存储可重载监听器

    public void addBannedItemId(int itemId) {
        List<Integer> current = BannedItemConfig.getIntegerList("banned-items");
        if (!current.contains(itemId)) {
            current.add(itemId);
            BannedItemConfig.set("banned-items", current);
            BannedItemConfig.save();
        }
    }
    public void removeBannedItemId(int itemId) {
        List<Integer> current = BannedItemConfig.getIntegerList("banned-items");
        if (current.remove(Integer.valueOf(itemId))) {
            BannedItemConfig.set("banned-items", current);
            BannedItemConfig.save();
        }
    }
    public Set<Integer> getBannedItemIds() {
        return new HashSet<>(BannedItemConfig.getIntegerList("banned-items"));
    }


    public List<Reloadable> getReloadableListeners() {
        return reloadableListeners;
    }
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.saveResource("BanItemConfig.yml", false);  // 确保配置文件存在
        this.BannedItemConfig = new Config(new File(this.getDataFolder(), "BanItemConfig.yml"), Config.YAML);


        registerReloadableListener(new BlockListener(this));
        registerReloadableListener(new CombatListener(this));  // 新增
        registerReloadableListener(new MovingListener(this));  // 新增
        registerReloadableListener(new LagListener(this));     // 新增
        registerReloadableListener(new InventoryListener(this));
        // 初始化监听器并保存引用
        this.getServer().getCommandMap().register("sad", new SadCommand(this));
        this.getServer().getCommandMap().register("sadbase", new SadBaseCommand(this));
        this.getLogger().info(TextFormat.GREEN + "SadAC 反作弊插件已启用！");
    }
    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED + "SadAC 反作弊插件已禁用！");
    }

    // 添加监听器获取方法

    public Config getCustomConfig() {
        return config;
    }
    public Config getBannedItemConfig() {return BannedItemConfig;}


}