package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.DropdownButtonComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class DropdownButtonComponentBeanInfo extends CommonBeanInfo {

    public DropdownButtonComponentBeanInfo() {
        super(
                DropdownButtonComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("border");
        removeProp("background");

        // -----------------------------
        // Data
        // -----------------------------
        addProp(
                "data",
                "Data",
                "Dataset with columns label, iconPath, and value. Divider rows are created when label is '-' or '--'.",
                CAT_DATA,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "selectedIndex",
                "Selected Index",
                "Currently selected row index. Use -1 for no selection.",
                CAT_DATA,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "selectedLabel",
                "Selected Label",
                "Currently selected label text.",
                CAT_DATA,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "hoverIndex",
                "Hover Index",
                "Current hovered row index. Use -1 when nothing is hovered.",
                CAT_DATA,
                BOUND_MASK
        );
        addProp(
                "text",
                "Text",
                "Optional fixed header text. If empty, the selected label or placeholder is shown.",
                CAT_DATA,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "placeholderText",
                "Placeholder Text",
                "Text displayed when no selected item exists.",
                CAT_DATA,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "open",
                "Open",
                "Shows or hides the popup dropdown list.",
                CAT_DATA,
                BOUND_MASK
        );

        // -----------------------------
        // Behavior
        // -----------------------------
        addProp(
                "enabled",
                "Enabled",
                "Enables or disables the dropdown button.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "headerHeight",
                "Header Height",
                "Height of the header button.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "rowHeight",
                "Row Height",
                "Height of each dropdown item row.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "showTopNotch",
                "Show Top Notch",
                "Shows the small top connector notch above the popup list.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "topNotchWidth",
                "Top Notch Width",
                "Width of the top connector notch.",
                CAT_BEHAVIOR,
                BOUND_MASK
        );
        addProp(
                "topNotchHeight",
                "Top Notch Height",
                "Height of the top connector notch.",
                CAT_BEHAVIOR,
                BOUND_MASK
        );

        // -----------------------------
        // Appearance - Header
        // -----------------------------
        addProp(
                "foreground",
                "Foreground Color",
                "Text color used for the header and popup items.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "headerBackground",
                "Header Background",
                "Background color of the header button.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "listBackground",
                "List Background",
                "Background color of the popup dropdown list.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "hoverBackground",
                "Hover Background",
                "Background color of hovered popup rows.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "hoverForeground",
                "Hover Foreground",
                "Text color of hovered popup rows.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "placeholderColor",
                "Placeholder Color",
                "Text color used for the placeholder.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "font",
                "Font",
                "Font used for the header and popup items.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "cornerRadius",
                "Corner Radius",
                "Controls the roundness of the header corners.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "strokeColor",
                "Stroke Color",
                "Border color of the header and popup list.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "strokeWidth",
                "Stroke Width",
                "Border width in pixels.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        // -----------------------------
        // Appearance - Header Icon
        // -----------------------------
        addProp(
                "iconPath",
                "Icon Path",
                "Optional header icon path.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addEnumProp(
                "iconLocation",
                "Icon Location",
                "Select header icon placement.",
                CAT_APPEARANCE,
                new int[]{
                        DropdownButtonComponent.ICON_LEFT,
                        DropdownButtonComponent.ICON_RIGHT
                },
                new String[]{
                        "Left",
                        "Right"
                }
        );
        addProp(
                "iconColor",
                "Icon Color",
                "Tint color applied to the header icon.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "iconSize",
                "Icon Size",
                "Header icon size in pixels.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "iconGap",
                "Icon Gap",
                "Gap between the header icon and the text.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        // -----------------------------
        // Appearance - Popup Items
        // -----------------------------
        addProp(
                "selectedItemIconPath",
                "Selected Item Icon Path",
                "Icon path used to mark the selected item. Leave empty to draw a checkmark.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addProp(
                "selectedItemIconColor",
                "Selected Item Icon Color",
                "Color of the selected row marker.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
        addEnumProp(
                "selectedItemIconPosition",
                "Selected Item Icon Position",
                "Controls whether the selected row marker is shown on the left or right.",
                CAT_APPEARANCE,
                new int[]{
                        DropdownButtonComponent.ICON_LEFT,
                        DropdownButtonComponent.ICON_RIGHT
                },
                new String[]{
                        "Left",
                        "Right"
                }
        );
        addEnumProp(
                "itemIconPosition",
                "Item Icon Position",
                "Controls whether dataset item icons are shown on the left or right.",
                CAT_APPEARANCE,
                new int[]{
                        DropdownButtonComponent.ICON_LEFT,
                        DropdownButtonComponent.ICON_RIGHT
                },
                new String[]{
                        "Left",
                        "Right"
                }
        );
        addProp(
                "itemIconSize",
                "Item Icon Size",
                "Size of dataset item icons.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Dropdown Button");
        bean.setDisplayName("Dropdown Button");
        bean.setShortDescription(
                "A dropdown button with an iOS-style header and a popup list with hover state, dividers, icons, and placeholder support."
        );
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