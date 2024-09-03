package net.cacpixel.rtmmetro.client.rtmtoolbox;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

public class RTMToolBox
{
    public static RTMToolBox instance = new RTMToolBox();
    public MainForm mainForm;

    public RTMToolBox()
    {

    }

    public static RTMToolBox getInstance()
    {
        return instance;
    }

    public void init()
    {
        try
        {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        }
        catch (Exception ex)
        {
            System.err.println("Failed to initialize LaF");
        }

        this.mainForm = new MainForm();
        this.mainForm.setVisible(true);

    }
}
