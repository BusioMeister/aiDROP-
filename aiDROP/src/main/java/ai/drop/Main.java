package ai.drop;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Plugin getPlugin() {
        return null;
    }

    @Override
    public void onEnable() {
        getCommand("drop").setExecutor(new DropCommand());
        getCommand("kilof").setExecutor(new kilof());
        getCommand("ci").setExecutor(new ci());
        getCommand("gm").setExecutor(new Gamemode());
        getCommand("gamemode").setExecutor(new Gamemode());

        getServer().getPluginManager().registerEvents(new DropListener(), this);

    }
}
