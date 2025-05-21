package me.sad.ac.command;

import cn.nukkit.utils.Config;
import me.sad.ac.SadAC;
import me.sad.ac.listener.Reloadable;
import me.sad.ac.listener.block.BlockListener;
import me.sad.ac.listener.combat.CombatListener;
import me.sad.ac.listener.moving.MovingListener;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.listener.server.LagListener;

public class SadBaseCommand extends Command {
    private final SadAC plugin;


    public SadBaseCommand(SadAC plugin) {
        super("sadbase", "重新加载反作弊配置", "/sadbase reload");
        this.plugin = plugin;
        this.setPermission("sadac.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("sadac.admin")) {
            sender.sendMessage(TextFormat.RED + "你没有权限使用此命令！");
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reload(sender);
            return true;
        }
        sender.sendMessage(TextFormat.RED + "用法: /sadbase reload");
        return false;
    }

    /**
     * 重新加载配置并初始化监听器
     * @param sender 命令发送者
     */
    private void reload(CommandSender sender) {
        plugin.getLogger().info("开始重新加载配置文件");
        plugin.saveConfig();
        plugin.reloadConfig();
        Config config = plugin.getCustomConfig();
        for (Reloadable listener : plugin.getReloadableListeners()) {
            listener.reloadConfig();
        }
        sender.sendMessage(TextFormat.GREEN + "反作弊配置已重新加载！");
        plugin.getLogger().info("配置文件重新加载完成");

    }
}