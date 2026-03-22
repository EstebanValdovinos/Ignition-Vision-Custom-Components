package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.CardComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class CardComponentBeanInfo extends CommonBeanInfo {

    public CardComponentBeanInfo() {
        super(
                CardComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("foreground");
        removeProp("font");
        removeProp("border");

        addProp(
                "background",
                "Background",
                "Controls the background color of the card.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "shadowColor",
                "Shadow Color",
                "Controls the color of the vanishing shadow.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "cornerRadius",
                "Corner Radius",
                "Controls the roundness of the card corners.",
                CAT_APPEARANCE,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "borderColor",
                "Border Color",
                "Controls the color of the card border.",
                CAT_APPEARANCE,
                BOUND_MASK
        );

        addProp(
                "borderWidth",
                "Border Width",
                "Controls the width of the card border. Use 0 to disable.",
                CAT_APPEARANCE,
                BOUND_MASK
        );

        addProp(
                "showShadow",
                "Show Shadow",
                "Enables or disables the shadow effect.",
                CAT_APPEARANCE,
                BOUND_MASK
        );

        addProp(
                "angle",
                "Angle",
                "Controls the angle of the shadow. Use 0 for centered shadow.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "elevation",
                "Elevation",
                "Controls the depth and spread of the shadow.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp(
                "shadowOpacity",
                "Shadow Opacity",
                "Controls how dense the shadow appears. Range: 0.0 to 1.0.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Card");
        bean.setDisplayName("Card");
        bean.setShortDescription("A custom card component with a soft vanishing shadow effect.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/card_component.png")
                ).getImage();

            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/card_component.png")
                ).getImage();
        }
        return null;
    }
}