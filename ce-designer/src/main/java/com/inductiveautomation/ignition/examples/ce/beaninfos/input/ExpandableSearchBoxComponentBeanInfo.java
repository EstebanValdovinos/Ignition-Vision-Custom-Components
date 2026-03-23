package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.ExpandableSearchBoxComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class ExpandableSearchBoxComponentBeanInfo extends CommonBeanInfo {

    public ExpandableSearchBoxComponentBeanInfo() {
        super(
                ExpandableSearchBoxComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("border");

        addProp("searchText", "Search Text", "The current search text.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("expanded", "Expanded", "True while the popup search field is expanded.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("enabled", "Enabled", "Enables or disables the component.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("expandedWidth", "Expanded Width", "Width of the popup search field.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "mode",
                "Mode",
                "Selects whether the component behaves as an expandable search button or as a fixed search box.",
                CAT_BEHAVIOR,
                new int[]{
                        ExpandableSearchBoxComponent.MODE_EXPANDABLE,
                        ExpandableSearchBoxComponent.MODE_FIXED
                },
                new String[]{
                        "Expandable",
                        "Fixed"
                }
        );

        addProp(
                "deferUpdates",
                "Defer Updates",
                "If true, updates searchText only when Enter is pressed.",
                CAT_BEHAVIOR,
                PREFERRED_MASK | BOUND_MASK
        );

        addProp("background", "Background Color", "Fill color of the search button and popup field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "Input text color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "Font used for the input text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("borderColor", "Border Color", "Border color of the main button and popup field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("borderWidth", "Border Width", "Border width in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Corner radius of the popup field. Use -1 for pill shape.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("iconColor", "Icon Color", "Color of the search icon and clear icon in expandable mode.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconSize", "Icon Size", "Size of the search and clear icons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "iconPosition",
                "Icon Position",
                "Controls whether the search icon is placed on the left or right side of the popup field.",
                CAT_APPEARANCE,
                new int[]{
                        ExpandableSearchBoxComponent.ICON_LEFT,
                        ExpandableSearchBoxComponent.ICON_RIGHT
                },
                new String[]{
                        "Left",
                        "Right"
                }
        );

        addEnumProp(
                "expandDirection",
                "Expand Direction",
                "Controls whether the popup field opens to the right or to the left of the main button.",
                CAT_APPEARANCE,
                new int[]{
                        ExpandableSearchBoxComponent.EXPAND_RIGHT,
                        ExpandableSearchBoxComponent.EXPAND_LEFT
                },
                new String[]{
                        "Right",
                        "Left"
                }
        );

        addProp("placeholderText", "Placeholder Text", "Placeholder text shown when the search box is empty.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("placeholderColor", "Placeholder Color", "Color of the placeholder text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Expandable Search Box");
        bean.setDisplayName("Expandable Search Box");
        bean.setShortDescription("A search box component that supports expandable and fixed display modes.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/search_box_icon.png");
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