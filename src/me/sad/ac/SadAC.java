package me.sad.ac;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.command.SadBaseCommand;
import me.sad.ac.command.SadCommand;
import me.sad.ac.listener.block.BlockListener;
import me.sad.ac.listener.combat.CombatListener;
import me.sad.ac.listener.moving.MovingListener;
import me.sad.ac.listener.inv.InventoryListener;
import me.sad.ac.listener.server.LagListener;

public class SadAC extends PluginBase {

    private Config config;
    // 添加监听器引用
    private MovingListener movingListener;
    private BlockListener blockListener;
    private InventoryListener InventoryListener;

    private CombatListener combatListener;
    private LagListener lagListener;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();

        // 初始化监听器并保存引用
        this.movingListener = new MovingListener(this);
        this.blockListener = new BlockListener(this);
        this.InventoryListener = new InventoryListener(this);
        this.combatListener = new CombatListener(this);
        this.lagListener = new LagListener(this);
        this.getServer().getPluginManager().registerEvents(movingListener, this);
        this.getServer().getPluginManager().registerEvents(blockListener, this);
        this.getServer().getPluginManager().registerEvents(InventoryListener, this);
        this.getServer().getPluginManager().registerEvents(combatListener, this);
        this.getServer().getPluginManager().registerEvents(lagListener, this);
        this.getServer().getCommandMap().register("sad", new SadCommand(this));
        this.getServer().getCommandMap().register("sadbase", new SadBaseCommand(this,
                movingListener,
                blockListener,
                combatListener,
                lagListener
        ));
        this.getLogger().info(TextFormat.GREEN + "SadAC 反作弊插件已启用！");
    }

    // 添加监听器获取方法
    public MovingListener getMovingListener() {
        return movingListener;
    }

    public BlockListener getAntiInstantBreak() {
        return blockListener;
    }

    public InventoryListener getInventoryListener() {
        return InventoryListener;
    }
    public CombatListener getCombatListener() {
        return combatListener;
    }
    public LagListener getLagListener() {
        return lagListener;
    }
    public Config getCustomConfig() {
        return config;
    }

}