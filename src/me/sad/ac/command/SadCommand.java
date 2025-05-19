package me.sad.ac.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.sad.ac.SadAC;

public class SadCommand extends Command {
    private final SadAC plugin;
    private final Config config;

    public SadCommand(SadAC plugin) {
        // 更新命令描述和用法提示
        super("sad", "控制反作弊检测的开启、关闭和速度设置", "/sad [fly|speed|fastbreak|wallhack|reach|lag] [on|off|set <value>]");
        this.plugin = plugin;
        this.config = plugin.getCustomConfig();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            // 更新用法提示，新增 lag 类型
            sender.sendMessage(TextFormat.RED + "用法: /sad [fly|speed|fastbreak|wallhack|reach|lag] [on|off|set <param> <value>]");
            return false;
        }

        String type = args[0].toLowerCase();
        String action = args[1].toLowerCase();

        switch (type) {
            case "fly":
                handleFlyCommand(sender, action, args);
                break;
            case "speed":
                handleSpeedCommand(sender, action, args);
                break;
            case "fastbreak":
                handleFastbreakCommand(sender, action, args);
                break;
            case "wallhack":
                handleWallhackCommand(sender, action, args);
                break;
            case "reach":
                handleReachCommand(sender, action, args);
                break;
            case "lag":  // 新增：处理 lag 类型命令
                handleLagCommand(sender, action, args);
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知类型: " + type + "，可用类型: fly, speed, fastbreak, wallhack, reach, lag");
                return false;
        }
        plugin.saveConfig();
        return true;
    }

    private void handleFlyCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.flight.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "飞行检测已开启");
                break;
            case "off":
                config.set("checks.flight.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "飞行检测已关闭");
                break;
            case "set":
                if (args.length >= 3) {
                    try {
                        double newSpeed = Double.parseDouble(args[2]);
                        config.set("checks.flight.max-speed", newSpeed);
                        sender.sendMessage(TextFormat.GREEN + "最大飞行速度已设置为: " + newSpeed);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextFormat.RED + "无效的速度值，请输入有效的数字");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad fly set <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }

    private void handleSpeedCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.speed.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "速度检测已开启");
                break;
            case "off":
                config.set("checks.speed.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "速度检测已关闭");
                break;
            case "set":
                if (args.length >= 3) {
                    try {
                        double newSpeed = Double.parseDouble(args[2]);
                        config.set("checks.speed.max-speed", newSpeed);
                        sender.sendMessage(TextFormat.GREEN + "最大走路移动速度已设置为: " + newSpeed);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextFormat.RED + "无效的速度值，请输入有效的数字");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad speed set <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }
    private void handleFastbreakCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.instant-break.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "秒破检测已开启");
                break;
            case "off":
                config.set("checks.instant-break.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "秒破已关闭");
                break;
            case "set":
                if (args.length >= 3) {
                    try {
                        double newSpeed = Double.parseDouble(args[2]);
                        config.set("checks.instant-break.min-break-time", newSpeed);
                        sender.sendMessage(TextFormat.GREEN + "最小破坏速度已设置为: " + newSpeed);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextFormat.RED + "无效的值，请输入有效的数字");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad fastbreak set <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }
    private void handleWallhackCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.wallhack.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "穿墙检测已开启");
                break;
            case "off":
                config.set("checks.wallhack.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "穿墙检测已关闭");
                break;
            case "set":
                if (args.length >= 3) {
                    String param = args[2].toLowerCase();
                    if (param.equals("steps")) {  // 设置采样步数
                        if (args.length >= 4) {
                            try {
                                int newSteps = Integer.parseInt(args[3]);
                                config.set("checks.wallhack.steps", newSteps);
                                sender.sendMessage(TextFormat.GREEN + "穿墙检测采样步数已设置为: " + newSteps);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(TextFormat.RED + "无效的步数，请输入有效的整数");
                            }
                        } else {
                            sender.sendMessage(TextFormat.RED + "用法: /sad wallhack set steps <value>");
                        }
                    } else {
                        sender.sendMessage(TextFormat.RED + "未知参数: " + param + "，可用参数: steps");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad wallhack set steps <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }
    private void handleReachCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.reach.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "距离检测（Reach）已开启");
                break;
            case "off":
                config.set("checks.reach.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "距离检测（Reach）已关闭");
                break;
            case "set":
                if (args.length >= 3) {
                    try {
                        double newDistance = Double.parseDouble(args[2]);
                        config.set("checks.reach.max-distance", newDistance);
                        sender.sendMessage(TextFormat.GREEN + "最大允许攻击距离已设置为: " + newDistance);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextFormat.RED + "无效的距离值，请输入有效的数字");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad reach set <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }

    private void handleLagCommand(CommandSender sender, String action, String[] args) {
        switch (action) {
            case "on":
                config.set("checks.lag.enabled", true);
                sender.sendMessage(TextFormat.GREEN + "防卡顿检测已开启");
                break;
            case "off":
                config.set("checks.lag.enabled", false);
                sender.sendMessage(TextFormat.GREEN + "防卡顿检测已关闭");
                break;
            case "set":
                if (args.length >= 4) {  // 需要至少 4 个参数：/sad lag set <param> <value>
                    String param = args[2].toLowerCase();
                    String valueStr = args[3];
                    try {
                        switch (param) {
                            case "redstone-interval":  // 设置高频红石更新间隔（毫秒）
                                int redstoneInterval = Integer.parseInt(valueStr);
                                config.set("checks.lag.redstone-update-interval", redstoneInterval);
                                sender.sendMessage(TextFormat.GREEN + "高频红石更新间隔已设置为: " + redstoneInterval + "ms");
                                break;
                            case "max-entities":  // 设置区块最大实体数阈值
                                int maxEntities = Integer.parseInt(valueStr);
                                config.set("checks.lag.max-entities-per-chunk", maxEntities);
                                sender.sendMessage(TextFormat.GREEN + "区块最大实体数阈值已设置为: " + maxEntities);
                                break;
                            default:
                                sender.sendMessage(TextFormat.RED + "未知参数: " + param + "，可用参数: redstone-interval, max-entities");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextFormat.RED + "无效的数值，请输入有效的整数");
                    }
                } else {
                    sender.sendMessage(TextFormat.RED + "用法: /sad lag set [redstone-interval|max-entities] <value>");
                }
                break;
            default:
                sender.sendMessage(TextFormat.RED + "未知操作: " + action + "，可用操作: on, off, set");
        }
    }
}