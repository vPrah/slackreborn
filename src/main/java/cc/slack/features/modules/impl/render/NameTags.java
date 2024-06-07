package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.render.LivingLabelEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.MathUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ModuleInfo(
        name = "NameTags",
        category = Category.RENDER
)
public class NameTags extends Module {

    @Listen
    public void onRender (RenderEvent event) {
        if (event.getState() == RenderEvent.State.RENDER_2D) {
            drawNameTags(event);
        }

    }

    @Listen
    public void onLivingLabel (LivingLabelEvent event) {
        if(event.getEntity() instanceof EntityPlayer && isValid((EntityPlayer) event.getEntity())) {
            event.cancel();
        }
    }

    public void drawNameTags(RenderEvent e) {
        if (e.getState() == RenderEvent.State.RENDER_2D) {
            mc.getWorld().loadedEntityList.forEach(entity -> {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer ent = (EntityPlayer) entity;
                    if (isValid(ent) && RenderUtil.isInViewFrustrum(ent.getEntityBoundingBox())) {
                        double posX = RenderUtil.polate(ent.posX, ent.lastTickPosX, e.getPartialTicks());
                        double posY = RenderUtil.polate(ent.posY, ent.lastTickPosY, e.getPartialTicks());
                        double posZ = RenderUtil.polate(ent.posZ, ent.lastTickPosZ, e.getPartialTicks());
                        double width = ent.width / 1.5;
                        double height = ent.height + (ent.isSneaking() ? -0.3 : 0.2);
                        AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
                        List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                        mc.getEntityRenderer().setupCameraTransform(e.getPartialTicks(), 0);
                        Vector4d position = null;
                        for (Vector3d vector : vectors) {
                            vector = RenderUtil.project(vector.field_181059_a - mc.getRenderManager().viewerPosX, vector.field_181060_b - mc.getRenderManager().viewerPosY, vector.field_181061_c - mc.getRenderManager().viewerPosZ);
                            if (vector != null && vector.field_181061_c >= 0.0 && vector.field_181061_c < 1.0) {
                                if (position == null) {
                                    position = new Vector4d(vector.field_181059_a, vector.field_181060_b, vector.field_181061_c, 0.0);
                                }
                                position.x = Math.min(vector.field_181059_a, position.x);
                                position.y = Math.min(vector.field_181060_b, position.y);
                                position.z = Math.max(vector.field_181059_a, position.z);
                                position.w = Math.max(vector.field_181060_b, position.w);
                            }
                        }
                        mc.getEntityRenderer().setupOverlayRendering();
                        if (position != null) {
                            GL11.glPushMatrix();
                            float size = .5f;
                            drawArmor(ent, (int) (position.x + ((position.z - position.x) / 2)), (int) position.y - 4 - mc.getFontRenderer().FONT_HEIGHT * 2, size);
                            GlStateManager.scale(size, size, size);
                            float x = (float) position.x / size;
                            float x2 = (float) position.z / size;
                            float y = (float) position.y / size;
                            final String nametext = entity.getDisplayName().getFormattedText() + " §7(§f" + MathUtil.roundToPlace(((EntityPlayer) entity).getHealth(), 2) + " §c❤§7)";
                            drawRoundedRect((x + (x2 - x) / 2) - (mc.getFontRenderer().getStringWidth(nametext) >> 1) - 2, y - mc.getFontRenderer().FONT_HEIGHT - 4, (x + (x2 - x) / 2) + (mc.getFontRenderer().getStringWidth(nametext) >> 1) + 2, y - 2, 8F, new Color(0, 0, 0, 120).getRGB());

                            mc.getFontRenderer().drawStringWithShadow(nametext, (x + ((x2 - x) / 2)) - (mc.getFontRenderer().getStringWidth(nametext) / 2F), y - mc.getFontRenderer().FONT_HEIGHT - 2, getNameColor(ent));
                            GL11.glPopMatrix();
                        }
                    }
                }
            });
        }
    }

    private int getNameColor(EntityLivingBase ent) {
        if (ent.getDisplayName().equals(mc.getPlayer().getDisplayName())) return new Color(0xFF99ff99).getRGB();
        return new Color(-1).getRGB();
    }


    private void drawArmor(EntityPlayer player, int x, int y, float size) {
        if (player.inventory.armorInventory.length > 0) {
            List<ItemStack> items = new ArrayList<>();
            if (player.getHeldItem() != null) {
                items.add(player.getHeldItem());
            }
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];
                if (stack != null) {
                    items.add(stack);
                }
            }
            int armorX = x - ((items.size() * 18) / 2);
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                GlStateManager.enableLighting();
                mc.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, armorX, y);
                mc.getMinecraft().getRenderItem().renderItemOverlays(mc.getFontRenderer(), stack, armorX, y);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                NBTTagList enchants = stack.getEnchantmentTagList();
                GlStateManager.pushMatrix();
                GlStateManager.scale(size, size, size);
                if (stack.getItem() == Items.golden_apple && stack.getMetadata() == 1) {
                    mc.getFontRenderer().drawString("op", armorX / size, y / size, 0xFFFF0000, true);
                }
                Enchantment[] important = new Enchantment[]{Enchantment.protection, Enchantment.sharpness, Enchantment.fireAspect, Enchantment.efficiency, Enchantment.power, Enchantment.flame};
                if (enchants != null) {
                    int ency = y + 8;
                    for (int index = 0; index < enchants.tagCount(); ++index) {
                        short id = enchants.getCompoundTagAt(index).getShort("id");
                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
                        Enchantment enc = Enchantment.getEnchantmentById(id);
                        for (Enchantment importantEnchantment : important) {
                            if (enc == importantEnchantment) {
                                String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                                if (level > 99) encName = encName + "99+";
                                else encName = encName + level;
                                mc.getFontRenderer().drawString(encName, armorX / size + 4, ency / size, 0xDDD1E6, true);
                                ency -= 5;
                                break;
                            }
                        }
                    }
                }
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                armorX += 18;
            }
        }
    }

    public boolean isValid(EntityLivingBase entity) {
        boolean players = true;
        boolean creatures = false;
        boolean villagers = false;
        boolean invisibles = false;

        return entity != null && (!(entity instanceof EntityArmorStand) && entity != mc.getPlayer() && (!entity.isInvisible() || invisibles) && !entity.isDead &&
                (entity.getHealth() != 0 && (!(entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityIronGolem ||
                        entity instanceof EntitySquid || entity instanceof EntityBat) || creatures) && (!(entity instanceof EntityVillager) || villagers) &&
                        (!(entity instanceof EntityOtherPlayerMP) || (players
                                && !entity.getDisplayName().getUnformattedText().contains("NPC ") && !entity.getDisplayName().equals("[NPC]") && !entity.getDisplayName().getUnformattedText().contains("CIT-") && !entity.getDisplayName().equals("")))));
    }

    public static void drawRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius, int color) {
        RenderUtil.drawRoundedRect(paramXStart, paramYStart, paramXEnd, paramYEnd, radius, color, true);
    }

}
