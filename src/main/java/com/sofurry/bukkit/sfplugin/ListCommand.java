/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.sofurry.bukkit.sfplugin;

import java.util.ArrayList;
import java.util.Collection;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand
implements CommandExecutor {
    Permission perms = null;

    public ListCommand(Permission perms) {
        this.perms = perms;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("SF.List")) {
            int i;
            ArrayList groups = new ArrayList();
            String[] rawgroups = this.perms.getGroups();
            int i2 = 0;
            while (i2 < rawgroups.length) {
                ArrayList<String> newgroup = new ArrayList<String>();
                newgroup.add(rawgroups[i2]);
                groups.add(newgroup);
                ++i2;
            }
            Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
            for (Player p : players) {
                String pgroup = this.perms.getPrimaryGroup(p);
                i = 0;
                while (i < groups.size()) {
                    if (((String)((ArrayList)groups.get(i)).get(0)).compareToIgnoreCase(pgroup) == 0) {
                        ((ArrayList)groups.get(i)).add(p.getName());
                    }
                    ++i;
                }
            }
            int i3 = groups.size() - 1;
            while (i3 >= 0) {
                if (((ArrayList)groups.get(i3)).size() <= 1) {
                    groups.remove(i3);
                }
                --i3;
            }
            String Compile = "";
            Boolean flip = true;
            i = 0;
            while (i < groups.size()) {
                int i22 = 0;
                while (i22 < ((ArrayList)groups.get(i)).size()) {
                    ArrayList group = (ArrayList)groups.get(i);
                    if (i22 == 0) {
                        Compile = String.valueOf(Compile) + (Object)ChatColor.GOLD + (String)group.get(i22) + ": ";
                    } else if (flip.booleanValue()) {
                        flip = false;
                        Compile = String.valueOf(Compile) + (Object)ChatColor.AQUA + (String)group.get(i22) + " ";
                    } else {
                        flip = true;
                        Compile = String.valueOf(Compile) + (Object)ChatColor.WHITE + (String)group.get(i22) + " ";
                    }
                    ++i22;
                }
                Compile = String.valueOf(Compile) + "\n";
                ++i;
            }
            sender.sendMessage(Compile);
            return true;
        }
        sender.sendMessage((Object)ChatColor.GOLD + "[SF Suite] " + (Object)ChatColor.BLUE + " You do not have permission to use this command.");
        return false;
    }
}
