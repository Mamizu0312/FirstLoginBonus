package com.github.mamizu0312.firstloginbonus;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class FirstLoginBonus extends JavaPlugin {
    MySQLManager mysql;
    String prefix = getConfig().getString("prefix");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mysql = new MySQLManager(this, "FirstLoginBonus");
        getCommand("FirstLoginBonus").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§eコンソールからの操作は禁止されています。");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("on")) {
                getConfig().set("status", "true");
                p.sendMessage(prefix + "§aFirstLoginBonusが有効化されました。");
                saveConfig();
                reloadConfig();
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                getConfig().set("status", "false");
                p.sendMessage(prefix + "§aFirstLoginBonusが無効化されました。");
                saveConfig();
                reloadConfig();
                return true;
            }
            return true;
        }

        return false;
    }

    public void onPlayerFirstLogin(PlayerJoinEvent e) {
        if(getConfig().getString("status").equalsIgnoreCase("true")) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Player p = e.getPlayer();
            String sql = "SELECT * FROM USERDATA WHERE UUID = '" + p.getUniqueId().toString() + "7;";
            ResultSet rs = mysql.query(sql);
            if (rs != null) {
                try {
                    if (rs.next()) {
                        mysql.close();
                        return;
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                    return;
                }
            }
            mysql.close();
            String sqls = "INSERT INTO USERDATA (MCID, UUID) VALUES('" + p.getName() + "','" + p.getUniqueId().toString() + ";";
            mysql.execute(sqls);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p+" minecraft:golden_shovel");
            p.sendMessage(prefix + "初回ログインボーナスを与えました！");
        });
    } else {
            return;
        }
}
}
