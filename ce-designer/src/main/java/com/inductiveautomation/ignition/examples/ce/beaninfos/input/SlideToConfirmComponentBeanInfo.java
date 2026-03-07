package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.SlideToConfirmComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class SlideToConfirmComponentBeanInfo extends CommonBeanInfo {

    public SlideToConfirmComponentBeanInfo() {
        super(SlideToConfirmComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");

        addProp("text", "Text", "The text displayed before sliding.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("activeText", "Active Text", "The text displayed as the knob reaches the end.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("confirmed", "Confirmed", "True when the control has been fully slid to the end.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("slidePos", "Slide Position", "Current slide position from 0.0 to 1.0.", CAT_DATA, BOUND_MASK);

        addProp("iconPath", "Icon Path", "Path to the icon displayed inside the knob.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "The track color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "The text color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("knobColor", "Knob Color", "The knob fill color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconColor", "Icon Color", "Tint color for the knob icon.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("pulseColor", "Pulse Color", "The ripple color shown when confirmation is reached.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("pulsePadding", "Pulse Padding", "Extra space reserved for the pulse effect so it stays inside component bounds.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "The font used for button text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Controls the roundness of the track corners. Use -1 for default.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("enabled", "Enabled", "Enables or disables the control.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("autoReset", "Auto Reset", "Automatically resets the slider after confirmation.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("resetDelay", "Reset Delay", "Delay in milliseconds before the slider resets.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("confirmThreshold", "Confirm Threshold", "Slide percentage required to confirm, from 0.1 to 1.0.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Slide To Confirm");
        bean.setDisplayName("Slide To Confirm");
        bean.setShortDescription("A draggable slide button that confirms when the knob reaches the end.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/slide_default_icon.png")
                ).getImage();

            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new javax.swing.ImageIcon(
                        getClass().getResource("/images/slide_default_icon.png")
                ).getImage();
        }
        return null;
    }


}