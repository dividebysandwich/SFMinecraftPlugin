package com.sofurry.bukkit.sfplugin;

import com.sofurry.bukkit.sfplugin.ListCommand;
import com.sofurry.bukkit.sfplugin.RentFly;
import com.sofurry.bukkit.sfplugin.SFRegisterCommand;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SFPlugin
extends JavaPlugin {
    public static Permission perms = null;
    public static Economy econ = null;

    public void onDisable() {
        this.getLogger().info("SF Registration Plugin disabled.");
    }

    public void onEnable() {
        this.getLogger().info("SF Registration Plugin starting");
        this.setupPermissions();
        this.setupEconomy();
        SFRegisterCommand sfRegisterCommand = new SFRegisterCommand();
        sfRegisterCommand.setLogger(this.getLogger());
        this.getCommand("sfregister").setExecutor((CommandExecutor)sfRegisterCommand);
        this.getCommand("sflist").setExecutor((CommandExecutor)new ListCommand(perms));
        this.getCommand("rentfly").setExecutor((CommandExecutor)new RentFly(econ, perms, this));
        PluginDescriptionFile pdfFile = this.getDescription();
        this.getLogger().info(String.valueOf(pdfFile.getName()) + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = (Economy)rsp.getProvider();
        if (econ != null) {
            return true;
        }
        return false;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        perms = (Permission)rsp.getProvider();
        if (perms != null) {
            return true;
        }
        return false;
    }
}
