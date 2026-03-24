package com.inductiveautomation.ignition.examples.ce.beaninfos.display;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.display.MessageLogCardComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.beans.IntrospectionException;

public class MessageLogCardComponentBeanInfo extends CommonBeanInfo {

    public MessageLogCardComponentBeanInfo() {
        super(
                MessageLogCardComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");

        addProp("title", "Title", "Title text shown in the header.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("message", "Message", "Message text shown in the body.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("logDate", "Date", "Date used to generate the formatted timestamp text.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("messageEditable", "Message Editable", "If true, clicking the message body allows inline editing.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("showDate", "Show Date", "If true, the formatted date is shown in the header.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("background", "Background Color", "Background color of the message body.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "Text color of the message body.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("font", "Font", "Font used for the message body text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "horizontalAlign",
                "Horizontal Align",
                "Message text horizontal alignment.",
                CAT_APPEARANCE,
                new int[]{
                        MessageLogCardComponent.ALIGN_LEFT,
                        MessageLogCardComponent.ALIGN_CENTER,
                        MessageLogCardComponent.ALIGN_RIGHT
                },
                new String[]{
                        "Left",
                        "Center",
                        "Right"
                }
        );

        addEnumProp(
                "verticalAlign",
                "Vertical Align",
                "Message text vertical alignment.",
                CAT_APPEARANCE,
                new int[]{
                        MessageLogCardComponent.ALIGN_TOP,
                        MessageLogCardComponent.ALIGN_MIDDLE,
                        MessageLogCardComponent.ALIGN_BOTTOM
                },
                new String[]{
                        "Top",
                        "Center",
                        "Bottom"
                }
        );
        addProp("titleBgColor", "Title Background", "Background color of the title header.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("titleTextColor", "Title Foreground", "Text color of the title header.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("titleFont", "Title Font", "Font used for the title text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("dateFont", "Date Font", "Font used for the date text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("dateColor", "Date Color", "Color used for the date text.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("showIcon", "Show Icon", "If true, the icon is shown in the header.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconPath", "Icon Path", "Ignition image path for the optional header icon. Leave empty to use the default icon.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconColor", "Icon Color", "Tint color applied to the header icon.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconSize", "Icon Size", "Header icon size in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("shadowOpacity", "Shadow Opacity", "Opacity of the vanishing shadow from 0.0 to 1.0.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("shadowColor", "Shadow Color", "Color used for the vanishing shadow.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("shadowDepth", "Shadow Depth", "Depth of the vanishing shadow in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("cornerRadius", "Corner Radius", "Roundness of the card corners.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("headerHeight", "Header Height", "Height of the title header section.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("padding", "Padding", "Inner padding used across the card layout.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Message Log Card");
        bean.setDisplayName("Message Log Card");
        bean.setShortDescription("A modern message log card with editable message body, formatted date text, icon support, alignment, tooltip support, shadow, and header click event.");
    }

    @Override
    public java.awt.Image getIcon(int kind) {
        switch (kind) {
            case java.beans.BeanInfo.ICON_COLOR_16x16:
            case java.beans.BeanInfo.ICON_MONO_16x16:
            case java.beans.BeanInfo.ICON_COLOR_32x32:
            case java.beans.BeanInfo.ICON_MONO_32x32:
                return new ImageIcon(
                        getClass().getResource("/images/message_log_icon.png")
                ).getImage();
        }
        return null;
    }
}