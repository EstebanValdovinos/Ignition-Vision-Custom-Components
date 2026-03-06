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
        addProp("enabled", "Enabled", "Enables or disables the button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("iOS Button");
        bean.setDisplayName("iOS Button");
        bean.setShortDescription("An iOS-style rounded button component.");
    }
}