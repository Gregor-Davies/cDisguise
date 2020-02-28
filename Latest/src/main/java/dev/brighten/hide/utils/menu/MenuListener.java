package dev.brighten.hide.utils.menu;

import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.XMaterial;
import dev.brighten.hide.Disguise;
import dev.brighten.hide.utils.menu.button.Button;
import dev.brighten.hide.utils.menu.button.ClickAction;
import dev.brighten.hide.utils.menu.type.BukkitInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 2/21/2018
 */
@Init
public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        final InventoryView inventoryView = event.getView();
        final Inventory inventory = inventoryView.getTopInventory();

        if (inventory.getHolder() instanceof BukkitInventoryHolder && ((Player) event.getWhoClicked()).isOnline()) {
            Menu menu = ((BukkitInventoryHolder) inventory.getHolder()).getMenu();

            event.setCancelled(true);

            if (menu != null) {
                final ItemStack stack = event.getCurrentItem();
                if ((stack == null || stack.getType() == XMaterial.AIR.parseMaterial()))
                    return;

                int slot = event.getSlot();
                if (slot >= 0 && slot <= menu.getMenuDimension().getSize()) {

                    Optional<Button> buttonOptional = menu.getButtonByIndex(slot);

                    buttonOptional.ifPresent(button -> {

                        if (button.getConsumer() == null) { // Allows for Buttons to not have an action.
                            return;
                        }
                        button.getConsumer().accept((Player) event.getWhoClicked(),
                                new ClickAction.InformationPair(button, event.getClick(), menu));

                        if (!button.isMoveable()) {
                            event.setResult(Event.Result.DENY);
                            event.setCancelled(true);
                        }
                    });
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        final InventoryView inventoryView = event.getView();
        final Inventory inventory = inventoryView.getTopInventory();

        if (inventory.getHolder() instanceof BukkitInventoryHolder) {
            Menu menu = ((BukkitInventoryHolder) inventory.getHolder()).getMenu();

            if (menu != null) {
                menu.handleClose((Player) event.getPlayer());

                menu.getParent().ifPresent(buttons -> new BukkitRunnable() {
                    public void run() {
                        if (event.getPlayer().getOpenInventory() == null
                                || (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder()
                                instanceof BukkitInventoryHolder))) {
                            buttons.showMenu((Player) event.getPlayer());
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Disguise.INSTANCE, 2L, 0L));
            }
        }
    }
}
