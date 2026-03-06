/* Filename: HelloWorldComponentBeanInfo.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ce.beaninfos;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.HelloWorldComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;


import java.beans.IntrospectionException;



/**
 * This BeanInfo class describes the {@link HelloWorldComponent}, which is the component that this example module adds
 * to the Vision Module
 *
 * @author Carl Gould
 */
public class HelloWorldComponentBeanInfo extends CommonBeanInfo {

    public HelloWorldComponentBeanInfo() {
        /*
         * Our superclass constructor takes the class of the component we describe and the customizers that are
         * applicable
         */
        super(HelloWorldComponent.class, DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR, StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("foreground");
        removeProp("background");
        removeProp("opaque");
        removeProp("font");

        addProp("enabled", "Enabled", "If disabled, the component can not be used.", CAT_COMMON, PREFERRED_MASK | BOUND_MASK);
        addProp("selected", "Selected", "True when the toggle is ON.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("trackOnColor", "Track On Color", "Background track color when ON.", CAT_APPEARANCE,PREFERRED_MASK | BOUND_MASK);
        addProp("trackOffColor", "Track Off Color", "Background track color when OFF.", CAT_APPEARANCE, BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("iOS Toggle");
        bean.setDisplayName("iOS Toggle");
        bean.setShortDescription("An iOS-style toggle switch component.");
        // Comment out until we update the term finder
        // bean.setValue(CommonBeanInfo.TERM_FINDER_CLASS, HelloWorldComponentTermFinder.class);
    }
}
