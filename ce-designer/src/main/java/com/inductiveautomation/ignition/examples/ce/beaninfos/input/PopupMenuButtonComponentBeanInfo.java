package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.PopupMenuButtonComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class PopupMenuButtonComponentBeanInfo extends CommonBeanInfo {

    public PopupMenuButtonComponentBeanInfo() {
        super(
                PopupMenuButtonComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("background");

        addProp("text", "Text", "Text shown in the main button.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("options", "Options", "Dataset with columns: option, iconPath.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedItem", "Selected Item", "Currently selected popup item label.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("hoverIndex", "Hover Index", "Current hovered popup item index. Use -1 when nothing is hovered.", CAT_DATA, BOUND_MASK);
        addProp("isOpen", "Is Open", "True while the popup is visible.", CAT_DATA, BOUND_MASK);

        addProp("enabled", "Enabled", "Enables or disables the component.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("cellWidth", "Cell Width", "Width of each popup menu cell.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("cellHeight", "Cell Height", "Height of each popup menu cell.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("iconSize", "Icon Size", "Popup icon size in pixels.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("popupGap", "Popup Gap", "Gap between the button and the popup menu.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "orientation",
                "Orientation",
                "Controls popup item arrangement.",
                CAT_BEHAVIOR,
                new int[]{
                        PopupMenuButtonComponent.ORIENTATION_VERTICAL,
                        PopupMenuButtonComponent.ORIENTATION_HORIZONTAL
                },
                new String[]{
                        "Vertical",
                        "Horizontal"
                }
        );

        addEnumProp(
                "popupLocation",
                "Popup Location",
                "Controls where the popup opens relative to the button.",
                CAT_BEHAVIOR,
                new int[]{
                        PopupMenuButtonComponent.POPUP_TOP,
                        PopupMenuButtonComponent.POPUP_BOTTOM,
                        PopupMenuButtonComponent.POPUP_LEFT,
                        PopupMenuButtonComponent.POPUP_RIGHT
                },
                new String[]{
                        "Top",
                        "Bottom",
                        "Left",
                        "Right"
                }
        );

        addProp("btnColor", "Button Color", "Background color of the main button.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("btnTextColor", "Button Text Color", "Text color of the main button.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("buttonTextFont", "Button Text Font", "Font used for the main button text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("menuBgColor", "Menu Background Color", "Background color of the popup menu.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("hoverColor", "Hover Color", "Background color of the hovered popup item.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedColor", "Selected Color", "Background color of the selected popup item.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("gridLineColor", "Grid Line Color", "Grid line color inside the popup menu.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("font", "Font", "Font used for popup option text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground", "Text color used for popup option text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Popup Menu Button");
        bean.setDisplayName("Popup Menu Button");
        bean.setShortDescription("A button that opens a dataset-driven popup menu using a borderless window.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/dropdown_button_icon.png");
        if (url == null) {
            return null;
        }

        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new ImageIcon(url).getImage();
            default:
                return null;
        }
    }
}