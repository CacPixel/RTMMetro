package net.cacpixel.rtmmetro.client.gui;

import jp.ngt.ngtlib.gui.GuiScreenCustom;
import net.cacpixel.rtmmetro.RTMMetro;
import net.cacpixel.rtmmetro.network.PacketRigidCatenarySettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GUIRigidCatenarySettings extends GuiScreenCustom {

    private static final int BUTTON_OK = 0;
    private static final int BUTTON_CANCEL = 1;

    GuiTextField testField1;


    public GUIRigidCatenarySettings() {
    }

    public GUIRigidCatenarySettings(EntityPlayer player, World world, int x, int y, int z) {
        ;
    }


    @Override
    public void initGui() {
        super.initGui();

        int hw = this.width / 2;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(BUTTON_OK, hw - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(BUTTON_CANCEL, hw + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));

    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case BUTTON_OK:
                this.sendPacket();
                this.mc.setIngameFocus();
                break;
            case BUTTON_CANCEL:
                this.mc.setIngameFocus();
                break;
            default:
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        int halfWidth = this.width / 2;
        this.drawCenteredString(this.fontRenderer, "rigid catenary gui test", halfWidth, 86, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Anchor Yaw", this.width - 70, 10, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Anchor Pitch", this.width - 70, 40, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Anchor Length H", this.width - 70, 70, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Anchor Length V", this.width - 70, 100, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Cant Center", this.width - 70, 130, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "Cant Edge", this.width - 70, 160, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, "中文测试", this.width - 70, 190, 0xFFFFFF);
    }

    private void sendPacket() {
        this.updateValues();
        RTMMetro.NETWORK_WRAPPER.sendToServer(new PacketRigidCatenarySettings());
    }

    private void updateValues() {
        ;
    }

}
