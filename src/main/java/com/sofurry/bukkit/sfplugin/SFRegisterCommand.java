package com.sofurry.bukkit.sfplugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SFRegisterCommand
implements CommandExecutor {
    Logger logger;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;
        HttpURLConnection conn = null;
        if (sender.hasPermission("sofurry.register")) {
            String result;
            result = "";
            try {
                String urlString = "https://www.sofurry.com/api/minecraftRegister?format=json&mcuser=" + player.getUniqueId();
                this.logger.info("HTTP Requesting: " + urlString);
                URL url = new URL(urlString);
                InputStream is = null;
                conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36");
                is = conn.getInputStream();
                Scanner s = new Scanner(is);
                s.useDelimiter("\\A");
                result = s.hasNext() ? s.next() : "";
                s.close();
                this.logger.info("HTTP result: " + result);
                try {
                    JSONObject json = new JSONObject(result);
                    result = json.getJSONObject("data").getString("hash");
                }
                catch (Exception e) {
                    this.logger.warning("Exception parsing result from SoFurry: " + e.getMessage());
                    return false;
                }
            }
            catch (Exception e) {
                this.logger.warning("Exception during HTTP request: " + e.getMessage());
                try {
                    BufferedReader br;
                    if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                        br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    } else {
                        br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                    }
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    this.logger.info("HTTP error output: " + result);
                } catch (Exception ex) {
                    this.logger.warning("Exception reading HTTP error body: " + ex.getMessage());
                }
                return false;
            }
            String registrationUrl = "https://www.sofurry.com/minecraft/verify?hash=" + result;
            player.sendMessage("Please visit the following URL to register your Minecraft name with SoFurry:");
            player.sendMessage(registrationUrl);
            return true;
        }
        player.sendMessage("Sorry you don't have access to this command.");
        return false;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}