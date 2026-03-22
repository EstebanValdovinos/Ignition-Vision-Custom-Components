package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.IOSButtonComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class IOSButtonComponentBeanInfo extends CommonBeanInfo {

    public IOSButtonComponentBeanInfo() {
        super(IOSButtonComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");

        addProp("text", "Text", "The text displayed on the button.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("foreground", "Foreground Color", "The text color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "The button fill color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "The font used for the button text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Controls the roundness of the button corners. Use -1 for the default pill shape.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("iconPath", "Icon Path", "Ignition image path for the optional button icon. Leave empty for no icon.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "iconLocation",
                "Icon Location",
                "Select icon placement.",
                CAT_APPEARANCE,
                new int[]{
                        IOSButtonComponent.ICON_LEFT,
                        IOSButtonComponent.ICON_RIGHT
                },
                new String[]{
                        "Left",
                        "Right"
                }
        );

        addProp(
                "strokeColor",
                "Stroke Color",
                "Border color of the button.",
                CAT_APPEARANCE,
                BOUND_MASK
        );

        addProp(
                "strokeWidth",
                "Stroke Width",
                "Border width in pixels. Use 0 to disable the border.",
                CAT_APPEARANCE,
                BOUND_MASK
        );

        addProp("iconColor", "Icon Color", "Tint color applied to the icon.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconSize", "Icon Size", "Icon size in pixels. Use 0 for automatic sizing.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconGap", "Icon Gap", "Gap in pixels between the icon and the centered text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("enabled", "Enabled", "Enables or disables the button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Rounded Button");
        bean.setDisplayName("Rounded Button");
        bean.setShortDescription("An iOS-style rounded button component.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/button_icon.png")
                ).getImage();

            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/button_icon.png")
                ).getImage();
        }
        return null;
    }
}