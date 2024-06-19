package cc.slack.features.modules.impl.utilties;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.other.TimeUtil;
import com.google.common.collect.Multimap;
import io.github.nevalackin.radbus.Listen;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@ModuleInfo(
        name = "AutoRod",
        category = Category.UTILITIES
)
public class AutoRod extends Module {


    private final NumberValue<Double> delay = new NumberValue<>("Delay", 100.0D, 50.0D, 1000.0D, 50.0D);
    private final BooleanValue autodisable = new BooleanValue("AutoDisable", false);

    private final TimeUtil time = new TimeUtil();
    private final TimeUtil time2 = new TimeUtil();
    private Boolean switchBack = false;
    private Boolean useRod = false;

    public AutoRod() {
        addSettings(delay, autodisable);
    }

  @SuppressWarnings("unused")
  @Listen
  public void onUpdate (UpdateEvent event) {
      int item = Item.getIdFromItem(mc.thePlayer.getHeldItem().getItem());
      float rodDelay = (delay.getValue()).floatValue();
      if (mc.getMinecraft().currentScreen == null) {
          if (!(Boolean) autodisable.getValue()) {
              if (!useRod && item == 346) {
                  Rod();
                  useRod = true;
              }

              if (time.isDelayComplete(rodDelay - 50.0F) && switchBack) {
                  switchBack();
                  switchBack = false;
              }

              if (time.isDelayComplete(rodDelay) && useRod) {
                  useRod = false;
              }
          } else if (item == 346) {
              if (time2.isDelayComplete(rodDelay + 200.0F)) {
                  Rod();
                  time2.reset();
              }

              if (time.isDelayComplete(rodDelay)) {
                  mc.thePlayer.inventory.currentItem = bestWeapon(mc.thePlayer);
                  time.reset();
                  toggle();
              }
          } else if (time.isDelayComplete(100L)) {
              switchToRod();
              time.reset();
          }
      }
  }

    public static int bestWeapon(Entity target) {
        Minecraft mc = Minecraft.getMinecraft();
        int firstSlot = mc.thePlayer.inventory.currentItem = 0;
        int bestWeapon = -1;
        int j = 1;

        for(byte i = 0; i < 9; ++i) {
            mc.thePlayer.inventory.currentItem = i;
            ItemStack itemStack = mc.thePlayer.getHeldItem();
            if (itemStack != null) {
                int itemAtkDamage = (int)getItemAtkDamage(itemStack);
                itemAtkDamage = (int)((float)itemAtkDamage + EnchantmentHelper.func_152377_a(itemStack, EnumCreatureAttribute.UNDEFINED));
                if (itemAtkDamage > j) {
                    j = itemAtkDamage;
                    bestWeapon = i;
                }
            }
        }

        if (bestWeapon != -1) {
            return bestWeapon;
        } else {
            return firstSlot;
        }
    }

    public static float getItemAtkDamage(ItemStack itemStack) {
        Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty()) {
            Iterator iterator = multimap.entries().iterator();
            if (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
                double damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? attributeModifier.getAmount() : attributeModifier.getAmount() * 100.0;
                if (attributeModifier.getAmount() > 1.0) {
                    return 1.0F + (float)damage;
                }

                return 1.0F;
            }
        }

        return 1.0F;
    }

    private int findRod(int startSlot, int endSlot) {
        for(int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == Items.fishing_rod) {
                return i;
            }
        }

        return -1;
    }

    private void switchToRod() {
        for(int i = 36; i < 45; ++i) {
            ItemStack itemstack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemstack != null && Item.getIdFromItem(itemstack.getItem()) == 346) {
                mc.thePlayer.inventory.currentItem = i - 36;
                break;
            }
        }

    }

    private void Rod() {
        int rod = findRod(36, 45);
        mc.getPlayerController().sendUseItem(mc.thePlayer, mc.getWorld(), mc.thePlayer.inventoryContainer.getSlot(rod).getStack());
        switchBack = true;
        time.reset();
    }

    private void switchBack() {
        mc.thePlayer.inventory.currentItem = bestWeapon(mc.thePlayer);
    }

}
