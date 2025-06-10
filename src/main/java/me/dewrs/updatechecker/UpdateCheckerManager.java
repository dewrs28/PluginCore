package me.dewrs.updatechecker;

import me.dewrs.logger.LogSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheckerManager {
    private final String version;
    private String latestVersion;
    private final String pluginName;

    public UpdateCheckerManager(String version, String pluginName) {
        this.version = version;
        this.pluginName = pluginName;
    }

    public UpdateCheckerResult check(int spigotId){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource="+spigotId).openConnection();
            int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (latestVersion.length() <= 7) {
                if(!version.equals(latestVersion)){
                    return UpdateCheckerResult.noErrors(latestVersion);
                }
            }
            return UpdateCheckerResult.noErrors(null);
        } catch (Exception ex) {
            return UpdateCheckerResult.error();
        }
    }

    public void manageUpdateChecker(int spigotId){
        if (!this.check(spigotId).isError()) {
            String latestVersion = this.check(spigotId).getLatestVersion();
            if (latestVersion != null) {
                LogSender.sendUpdateMessage(pluginName, version, latestVersion, spigotId);
            }
        } else {
            LogSender.sendErrorMessage("&cError while checking update.");
        }
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}