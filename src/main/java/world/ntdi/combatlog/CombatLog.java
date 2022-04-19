package world.ntdi.combatlog;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.WeakHashMap;

public final class CombatLog extends JavaPlugin implements Listener {
    public static WeakHashMap<Player, Integer> combatLog = new WeakHashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        onTick();
        Bukkit.getLogger().info("CombatLog has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("CombatLog has been disblaed!");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player d = (Player) e.getDamager();
            Player p = (Player) e.getEntity();
            setInCombat(d);
            setInCombat(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isInCombat(e.getPlayer())) {
            combatLog.remove(e.getPlayer());
            Player p = e.getPlayer();
            for (ItemStack i : p.getInventory()) {
                if (i != null || i.getType() != Material.AIR) {
                    p.getWorld().dropItem(p.getLocation(), i);
                }
            }
        }
    }

    public static void onTick() {
        for (Player player : combatLog.keySet()) {
            combatLog.put(player, combatLog.get(player) - 1);
            if (combatLog.get(player) <= 0) {
                combatLog.remove(player);
                player.sendTitle(null, "You are no longer in combat.", 1, 20, 1);
            }
        }
    }

    public static boolean isInCombat(Player player) {
        return combatLog.containsKey(player);
    }

    public static void setInCombat(Player player) {
        combatLog.put(player, 10);
    }
}
