package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.SegmentedControlComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class SegmentedControlComponentBeanInfo extends CommonBeanInfo {

    public SegmentedControlComponentBeanInfo() {
        super(SegmentedControlComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("border");

        addProp("items", "Items", "Dataset with main columns label and icon. If icon is empty, text is shown. If label is empty, icon is shown only.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedIndex", "Selected Index", "Currently selected segment index. Use -1 for no selection.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedValue", "Selected Value", "Currently selected segment label. Setting this selects the matching row.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("enabled", "Enabled", "Enables or disables the control.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("animateSelection", "Animate Selection", "If true, the selected segment transitions smoothly.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        // Common appearance properties
        addEnumProp(
                "orientation",
                "Orientation",
                "Controls whether the segmented control is horizontal or vertical.",
                CAT_APPEARANCE,
                new int[]{
                        SegmentedControlComponent.ORIENTATION_HORIZONTAL,
                        SegmentedControlComponent.ORIENTATION_VERTICAL
                },
                new String[]{
                        "Horizontal",
                        "Vertical"
                }
        );
        addProp("hoverBackground", "Hover Background", "Background tint shown when hovering over a segment.", CAT_APPEARANCE, BOUND_MASK);
        addProp("borderColor", "Border Color", "Border color of the control.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("showDividers", "Show Dividers", "If true, divider lines are shown between segments.", CAT_APPEARANCE, BOUND_MASK);
        addProp("dividerColor", "Divider Color", "Divider line color between segments.", CAT_APPEARANCE, BOUND_MASK);
        addProp("borderWidth", "Border Width", "Border width in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Corner radius of the control. Use -1 for pill shape.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("segmentGap", "Segment Gap", "Gap between segments in pixels.", CAT_APPEARANCE, BOUND_MASK);
        addProp("padding", "Padding", "Inner padding of the control in pixels.", CAT_APPEARANCE, BOUND_MASK);
        addProp("iconSize", "Icon Size", "Icon size in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconGap", "Icon Gap", "Gap between icon and text when both are present.", CAT_APPEARANCE, BOUND_MASK);


        // Unselected appearance properties
        addProp("font", "Font", "Font used for unselected segment text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "Background color of the control.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "Text color of unselected segments.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconColor", "Icon Color", "Tint color used for unselected icons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        // Selected appearance properties
        addProp("selectedFont", "Selected Font", "Font used for selected segment text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedBackground", "Selected Background", "Fill color of the selected segment.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedForeground", "Selected Foreground", "Text color of the selected segment.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedIconColor", "Selected Icon Color", "Tint color used for selected icons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);



    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Segmented Control");
        bean.setDisplayName("Segmented Control");
        bean.setShortDescription("A modern segmented selector supporting dataset-driven label and icon content, orientation switching, and animated selection.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/segmented_control_icon.png");
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