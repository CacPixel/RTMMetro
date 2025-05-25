package net.cacpixel.rtmmetro.client.gui;

public class ScissorParam
{
     GuiScreenAdvanced screen;
     public int x;
     public int y;
     public int width;
     public int height;

     public ScissorParam(GuiScreenAdvanced screen, int x, int y, int width, int height)
     {
          this.screen = screen;
          this.x = x;
          this.y = y;
          this.width = width;
          this.height = height;
     }

}
