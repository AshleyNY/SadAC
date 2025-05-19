一款基于nukkit-mot的用于无政府服务器的反作弊插件；
目前已经实现的功能如下：
Reach Limit——防止reach过远
AntiFastBreak——防止秒挖
InventoryGuard——防止刷物和违规附魔
Fly/SpeedLimit——飞行限速
NoPhase——禁止穿墙
Nolag——防止高频红石+清楚区块实体堆积

配置如下：
checks:
  flight:
    enabled: true     # 是否开启飞行速度检测
    max-speed: 750    # 最大速度（单位：Sad值区间在600-750比较ok）
  speed:
    enabled: true     # 是否开启速度检测
    max-speed: 750    # 最大速度（单位：Sad值区间在600-750比较ok）
  wallhack:
    enabled: true       # 是否开启穿墙检测
    steps: 3           # 路径采样步数（数值越大越精确，性能消耗越高）
  instant-break:
    enabled: true       # 是否开启秒挖检测
    min-break-time: 10 # 挖一个方块所需的最低时间（毫秒）
  reach:
    enabled: true       # 是否开启 Reach 检测
    max-reach: 5        # 最大 Reach 距离
  # 新增：Lag检测配置
  lag:
    enabled: true        # 是否开启 Lag 检测
    redstone-update-interval: 50    # 高频红石更新间隔阈值（毫秒）
    max-entities-per-chunk: 50      # 单个区块最大实体数阈值


命令如下：
/sadbase reload 重新加载配置
/sad [fly|speed|fastbreak|wallhack|reach|lag] [on|off|set <value>]
Ex.： 
/sad fly on
/sad fly set 750
/sad lag set redstone 50（高频限速最快速度1为最快 10000+为慢）
/sad lag set entities 50（玩家附近10个区块内实体最多的船和经验球数量）
