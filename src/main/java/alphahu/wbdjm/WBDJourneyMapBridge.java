// 养成写注释的好习惯是很重要的，不然第二天自己都不知自己写的什么。by：糊糊
package alphahu.wbdjm;

import com.warz.bombdefuse.WarZBombDefusePlugin;
import com.warz.bombdefuse.arena.ArenaSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class WBDJourneyMapBridge extends JavaPlugin {

    private WarZBombDefusePlugin wbd;

    // 原版队伍名前缀，避免和其他插件的队伍冲突
    private static final String TEAM_PREFIX = "wbd_";

    @Override
    public void onEnable() {
        getLogger().info("§b========================================");
        getLogger().info("§b  WBDJourneyMapBridge §ev" + getDescription().getVersion());
        getLogger().info("§b  作者：§dAlphaHu");
        getLogger().info("§b  对接 WarZBombDefuse，同步队伍到原版 Team，配合 JourneyMap Teams 隐藏敌军");
        getLogger().info("§b  意见反馈QQ：§d2387629002");
        getLogger().info("§b========================================");

        this.wbd = (WarZBombDefusePlugin) Bukkit.getPluginManager().getPlugin("WarZBombDefuse");

        if (wbd == null) {
            getLogger().severe("§c对接 WarZBombDefuse 失败：未找到该插件，本插件将自动卸载。");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!wbd.isEnabled()) {
            getLogger().severe("§c对接 WarZBombDefuse 失败：该插件未启用，本插件将自动卸载。");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("§a对接 WarZBombDefuse 状态：成功，加载完成。");

        // 每 20 tick（1 秒）同步一次队伍
        new BukkitRunnable() {
            @Override
            public void run() {
                syncAllPlayers();
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().info("§b========================================");
        getLogger().info("§b  WBDJourneyMapBridge §c已卸载");
        getLogger().info("§b  感谢使用，再见。");
        getLogger().info("§b========================================");

        // 清理本插件创建的所有原版队伍
        cleanupAllTeams();
    }

    private void syncAllPlayers() {
        if (Bukkit.getScoreboardManager() == null) return;
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            syncPlayer(sb, player);
        }
    }

    private void syncPlayer(Scoreboard sb, Player player) {
        ArenaSession session = wbd.getArenaManager().getSession(player);
        String targetTeamName = null;

        if (session != null) {
            // 先判断玩家是否存活，死亡的观战玩家不加入任何队伍
            boolean alive = session.getRecord(player).isAlive();

            if (alive) {
                Object team = session.getTeam(player);
                if (team != null) {
                    String teamStr = team.toString();
                    if ("T".equalsIgnoreCase(teamStr)) {
                        targetTeamName = TEAM_PREFIX + "T";
                    } else if ("CT".equalsIgnoreCase(teamStr)) {
                        targetTeamName = TEAM_PREFIX + "CT";
                    }
                }
            }
            // 如果 alive == false，targetTeamName 保持 null，玩家会被移出所有原版队伍
        }

        // 先把玩家从所有本插件创建的原版队伍中移除
        for (Team t : sb.getTeams()) {
            if (t.getName().startsWith(TEAM_PREFIX)) {
                if (t.hasEntry(player.getName())) {
                    t.removeEntry(player.getName());
                }
            }
        }

        // 只有活着的 T/CT 玩家才加入对应队伍
        if (targetTeamName != null) {
            Team team = sb.getTeam(targetTeamName);
            if (team == null) {
                team = sb.registerNewTeam(targetTeamName);
            }
            team.addEntry(player.getName());
        }
    }
// 当WBD的比赛结束后将自动清理队伍标签
    private void cleanupAllTeams() {
        if (Bukkit.getScoreboardManager() == null) return;
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team t : sb.getTeams()) {
            if (t.getName().startsWith(TEAM_PREFIX)) {
                t.unregister();
            }
        }
    }
}

