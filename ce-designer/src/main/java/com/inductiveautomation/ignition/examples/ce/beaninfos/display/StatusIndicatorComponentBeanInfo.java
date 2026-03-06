package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.StatusIndicatorComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import java.beans.IntrospectionException;

public class StatusIndicatorComponentBeanInfo extends CommonBeanInfo {

    public StatusIndicatorComponentBeanInfo() {
        super(StatusIndicatorComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("background");
        removeProp("opaque");

        addProp("text", "Text", "The text shown next to the indicator.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("on", "On", "Turns the indicator on or off.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "effectMode",
                "Effect Mode",
                "Controls the indicator effect.",
                CAT_BEHAVIOR,
                new int[]{
                        StatusIndicatorComponent.EFFECT_NONE,
                        StatusIndicatorComponent.EFFECT_BLINK,
                        StatusIndicatorComponent.EFFECT_PULSE
                },
                new String[]{
                        "None",
                        "Blink",
                        "Pulse"
                }
        );

        addProp("effectSpeed", "Effect Speed", "Controls the speed of blink or pulse in milliseconds.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "textSide",
                "Text Side",
                "Controls whether the text appears to the left or right of the indicator.",
                CAT_APPEARANCE,
                new int[]{
                        StatusIndicatorComponent.TEXT_RIGHT,
                        StatusIndicatorComponent.TEXT_LEFT
                },
                new String[]{
                        "Right",
                        "Left"
                }
        );

        addProp("statusColor", "On Color", "The color of the indicator when ON.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("offColor", "Off Color", "The color of the indicator when OFF.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "The text color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "The font used for the label text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Status Indicator");
        bean.setDisplayName("Status Indicator");
        bean.setShortDescription("A display component that shows a colored status indicator with text.");
    }
}