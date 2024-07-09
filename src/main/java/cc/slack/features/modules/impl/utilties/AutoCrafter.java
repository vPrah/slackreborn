package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.game.TickEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;

@ModuleInfo(
        name = "AutoCrafter",
        category = Category.UTILITIES
)
public class AutoCrafter extends Module {

    private final ModeValue<String> crafterMode = new ModeValue<>("Crafter", new String[]{"Boat", "Furnace"});

    public AutoCrafter() {
        addSettings(crafterMode);
    }

    @Override
    public void onEnable() {
        PrintUtil.message("Please, click on the crafting table.");
    }

    @Listen
    public void onTick (TickEvent event) {
        switch (crafterMode.getValue()) {
            case "Boat":
                if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiCrafting) {
                    int windowId = mc.thePlayer.openContainer.windowId;
                    for (Slot inventorySlot : mc.thePlayer.inventoryContainer.inventorySlots) {
                        if (inventorySlot.getHasStack() &&
                                (inventorySlot.getStack()).stackSize >= 6 &&
                                inventorySlot.getStack().getItem() instanceof ItemBlock && (
                                (ItemBlock)inventorySlot.getStack().getItem()).getBlock() == Blocks.log) {
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber + 1, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 1, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 0, 0, 1, mc.thePlayer);
                            return;
                        }
                    }
                    for (Slot inventorySlot : mc.thePlayer.inventoryContainer.inventorySlots) {
                        if (inventorySlot.getHasStack() &&
                                (inventorySlot.getStack()).stackSize >= 6 &&
                                inventorySlot.getStack().getItem() instanceof ItemBlock && (
                                (ItemBlock)inventorySlot.getStack().getItem()).getBlock() == Blocks.planks) {
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber + 1, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 4, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 7, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 8, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 9, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 6, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber + 1, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 0, 0, 1, mc.thePlayer);
                            for (Slot slot : mc.thePlayer.inventoryContainer.inventorySlots) {
                                if (slot.getHasStack() &&
                                        (slot.getStack()).stackSize >= 6 &&
                                        slot.getStack().getItem() instanceof ItemBlock && (
                                        (ItemBlock)slot.getStack().getItem()).getBlock() == Blocks.planks) {
                                    mc.playerController.windowClick(windowId, slot.slotNumber + 1, 0, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 4, 1, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 7, 1, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 8, 1, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 9, 1, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 6, 1, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, slot.slotNumber + 1, 0, 0, mc.thePlayer);
                                    mc.playerController.windowClick(windowId, 0, 0, 1, mc.thePlayer);
                                    mc.thePlayer.closeScreen();
                                    return;
                                }
                            }
                            mc.thePlayer.closeScreen();
                            return;
                        }
                    }
                }
                break;
            case "Furnace":
                if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiCrafting) {
                    int windowId = mc.thePlayer.openContainer.windowId;
                    for (Slot inventorySlot : mc.thePlayer.inventoryContainer.inventorySlots) {
                        if (inventorySlot.getHasStack() &&
                                (inventorySlot.getStack()).stackSize >= 8 &&
                                inventorySlot.getStack().getItem() instanceof ItemBlock && (
                                (ItemBlock)inventorySlot.getStack().getItem()).getBlock() == Blocks.cobblestone) {
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 1, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 2, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 3, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 4, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 6, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 7, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 8, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, 9, 1, 0, mc.thePlayer);
                            mc.playerController.windowClick(windowId, inventorySlot.slotNumber, 0, 0, mc.thePlayer);
                            
                            mc.playerController.windowClick(windowId, 0, 0, 1, mc.thePlayer);
                            mc.thePlayer.closeScreen();
                            return;
                        }
                    }
                }
                break;
        }
    }

}
