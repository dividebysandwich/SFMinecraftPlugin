package com.sofurry.bukkit.sfplugin;

import com.sofurry.bukkit.sfplugin.SFPlugin;
import java.util.Collection;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class RentFly
implements CommandExecutor, Runnable {
    private Player val$flier;
    Economy econ = null;
    Permission perms = null;
    SFPlugin sf = null;

    public RentFly(Economy econ, Permission perms, SFPlugin sf) {
        this.econ = econ;
        this.perms = perms;
        this.sf = sf;
    }
	
	private RentFly(Player player) {
        this.val$flier = player;
    }

    @Override
    public void run() {
        if (this.val$flier.hasPermission("SF.RentFly")) {
            if (RentFly.this.econ.getBalance((OfflinePlayer)this.val$flier) >= 50.0) {
                RentFly.this.econ.withdrawPlayer((OfflinePlayer)this.val$flier, 50.0);
                if (this.val$flier.getAllowFlight()) {
                    this.val$flier.sendMessage("Your flight was renewed for one minute");
                } else {
                    this.val$flier.setAllowFlight(true);
                    this.val$flier.setFlying(true);
                    this.val$flier.sendMessage("flight enabled, double tap space to fly");
                }
            } else {
                this.val$flier.setFlying(false);
                this.val$flier.setAllowFlight(false);
                this.val$flier.sendMessage("You ran out of money to pay for flight!");
            }
            if (RentFly.this.econ.getBalance((OfflinePlayer)this.val$flier) < 550.0 && RentFly.this.econ.getBalance((OfflinePlayer)this.val$flier) > 450.0) {
                this.val$flier.sendMessage("Warning: you can only afford another 9-10 minutes of flight!");
            }
        } else {
            this.val$flier.sendMessage("Your flight ended because you are no longer allowed to fly");
            this.val$flier.setFlying(false);
            this.val$flier.setAllowFlight(false);
        }
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("SF.RentFly")) {
            if (args.length >= 1) {
                final Player flier = this.getPlayer(sender.getName(), sender.getServer());
                BukkitScheduler scheduler = Bukkit.getScheduler();
                if (args[0].compareToIgnoreCase("start") == 0) {
                    if (this.econ.getBalance((OfflinePlayer)flier) >= 500.0) {
                        Thread t = new Thread(new Runnable(){

                            @Override
                            public void run() {
                                if (flier.hasPermission("SF.RentFly")) {
                                    if (RentFly.this.econ.getBalance((OfflinePlayer)flier) >= 50.0) {
                                        RentFly.this.econ.withdrawPlayer((OfflinePlayer)flier, 50.0);
                                        if (flier.getAllowFlight()) {
                                            flier.sendMessage("Your flight was renewed for one minute");
                                        } else {
                                            flier.setAllowFlight(true);
                                            flier.setFlying(true);
                                            flier.sendMessage("flight enabled, double tap space to fly");
                                        }
                                    } else {
                                        flier.setFlying(false);
                                        flier.setAllowFlight(false);
                                        flier.sendMessage("You ran out of money to pay for flight!");
                                    }
                                    if (RentFly.this.econ.getBalance((OfflinePlayer)flier) < 550.0 && RentFly.this.econ.getBalance((OfflinePlayer)flier) > 450.0) {
                                        flier.sendMessage("Warning: you can only afford another 9-10 minutes of flight!");
                                    }
                                } else {
                                    flier.sendMessage("Your flight ended because you are no longer allowed to fly");
                                    flier.setFlying(false);
                                    flier.setAllowFlight(false);
                                }
                            }
                        });
                        t.setName("RentFly" + flier.getName());
                        scheduler.scheduleSyncRepeatingTask((Plugin)this.sf, (Runnable)t, 0, 1200);
                        return true;
                    }
                    sender.sendMessage("You would not be able to purchase more than 10 minutes of fly, try again later");
                    return true;
                }
                if (args[0].compareToIgnoreCase("stop") == 0) {
                    Thread s = this.getThreadByName("RenyFly" + flier.getName());
                    if (s != null) {
                        scheduler.cancelTask((int)s.getId());
                        return true;
                    }
                    sender.sendMessage("You were not renting fly or your flight tracker crashed");
                    flier.setAllowFlight(false);
                    flier.setFlying(false);
                    return true;
                }
                sender.sendMessage("You provided an invalid argument");
                return false;
            }
            sender.sendMessage("You did not provide enough arguments");
            return false;
        }
        sender.sendMessage("You do not have SF.RentFly");
        return true;
    }

    private Player getPlayer(String name, Server server) {
        Collection<? extends Player> players = server.getOnlinePlayers();

        for (Player p : players) {
            if (name.compareTo(p.getName()) != 0) continue;
            return p;
        }
        return null;
    }

    public Thread getThreadByName(String threadName) {
        Thread __tmp = null;
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        int i = 0;
        while (i < threadArray.length) {
            if (threadArray[i].getName().equals(threadName)) {
                __tmp = threadArray[i];
            }
            ++i;
        }
        return __tmp;
    }

}
