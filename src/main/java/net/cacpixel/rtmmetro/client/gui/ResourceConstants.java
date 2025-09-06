package net.cacpixel.rtmmetro.client.gui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ResourceConstants
{
    public static final String TEXTURE_WIDGETS = "widgets";
    public static final String ICON_SAVE = "icon/save";
    public static final String ICON_MARKER_GROUP = "icon/marker_group";
    public static final String ICON_MARKER_NAME = "icon/marker_name";
    public static final String ICON_MARKER_EDIT_LINE = "icon/marker_edit_line";
    public static final String ICON_UP = "icon/up";
    public static final String ICON_DOWN = "icon/down";
    public static final String ICON_LEFT = "icon/left";
    public static final String ICON_RIGHT = "icon/right";
    public static final String ICON_CLOSE = "icon/close";
    public static final List<String> RESOURCE_LIST = new ArrayList<>();

    static
    {
        try
        {
            Field[] fields = ResourceConstants.class.getDeclaredFields();
            for (Field field : fields)
            {
                Object obj = field.get(null);
                if (obj instanceof String)
                {
                    RESOURCE_LIST.add((String) obj);
                }
            }
        }
        catch (SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
