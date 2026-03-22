package com.inductiveautomation.ignition.examples.ce.beaninfos.input;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.ignition.examples.ce.components.input.PaginationComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.net.URL;

public class PaginationComponentBeanInfo extends CommonBeanInfo {

    public PaginationComponentBeanInfo() {
        super(
                PaginationComponent.class,
                DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR,
                StyleCustomizer.VALUE_DESCRIPTOR
        );
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        super.initProperties();

        removeProp("opaque");
        removeProp("border");

        // -----------------------------
        // Data
        // -----------------------------
        addProp("currentPage", "Current Page", "Current selected page.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("totalPages", "Total Pages", "Total number of available pages.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);
        addProp("visiblePageCount", "Visible Page Count", "Number of visible page buttons in Numbers mode.", CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        // -----------------------------
        // Behavior
        // -----------------------------
        addEnumProp(
                "type",
                "Type",
                "Controls the pagination display mode.",
                CAT_BEHAVIOR,
                new int[]{
                        PaginationComponent.TYPE_INPUT,
                        PaginationComponent.TYPE_NUMBERS
                },
                new String[]{
                        "Input",
                        "Numbers"
                }
        );

        addProp("enabled", "Enabled", "Enables or disables the component.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showFirstButton", "Show First Button", "Shows the first page navigation button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showPreviousButton", "Show Previous Button", "Shows the previous page navigation button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showNextButton", "Show Next Button", "Shows the next page navigation button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showLastButton", "Show Last Button", "Shows the last page navigation button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showPageInput", "Show Page Input", "Shows the manual page input field in Input mode.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showTotalPages", "Show Total Pages", "Shows the total pages text in Input mode.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("editablePageInput", "Editable Page Input", "Allows the user to type a page number manually.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("showEllipsis", "Show Ellipsis", "Shows ellipsis when not all pages are visible in Numbers mode.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("alwaysShowFirstLastPage", "Always Show First/Last Page", "Always shows the first and last page numbers in Numbers mode.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("centerSelectedPage", "Center Selected Page", "Keeps the selected page centered when possible in Numbers mode.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("commitOnEnter", "Commit On Enter", "Commits the typed page when Enter is pressed.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("commitOnFocusLost", "Commit On Focus Lost", "Commits the typed page when the input loses focus.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("autoClampPage", "Auto Clamp Page", "Keeps the page value inside the valid page range.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        addEnumProp(
                "navLabelMode",
                "Navigation Label Mode",
                "Controls whether navigation buttons show icons, text, or both.",
                CAT_BEHAVIOR,
                new int[]{
                        PaginationComponent.NAV_LABEL_MODE_ICON_ONLY,
                        PaginationComponent.NAV_LABEL_MODE_TEXT_ONLY,
                        PaginationComponent.NAV_LABEL_MODE_ICON_AND_TEXT
                },
                new String[]{
                        "Icon Only",
                        "Text Only",
                        "Icon And Text"
                }
        );

        addProp("firstButtonText", "First Button Text", "Text label for the first-page button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("previousButtonText", "Previous Button Text", "Text label for the previous-page button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("nextButtonText", "Next Button Text", "Text label for the next-page button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);
        addProp("lastButtonText", "Last Button Text", "Text label for the last-page button.", CAT_BEHAVIOR, PREFERRED_MASK | BOUND_MASK);

        // -----------------------------
        // Appearance
        // -----------------------------
        addProp("font", "Font", "Font used by the pagination component.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("background", "Background Color", "Background color of the component.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("foreground", "Foreground Color", "Default text color for page numbers.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("borderColor", "Border Color", "Outer border color.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("borderWidth", "Border Width", "Outer border width in pixels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("cornerRadius", "Corner Radius", "Corner radius of the outer component background.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("padding", "Padding", "Inner padding around the content.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("buttonGap", "Button Gap", "Gap between navigation buttons and page items.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("buttonWidth", "Button Width", "Minimum width of the navigation buttons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("buttonHeight", "Button Height", "Height of the navigation buttons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("inputWidth", "Input Width", "Width of the manual page input field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("pageButtonMinWidth", "Page Button Min Width", "Minimum width of numeric page buttons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("hoverBackground", "Hover Background", "Background color for hovered buttons and pages.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedPageBackground", "Selected Page Background", "Background color for the selected page.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedPageForeground", "Selected Page Foreground", "Text color for the selected page.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("selectedPageBorderColor", "Selected Page Border Color", "Border color for the selected page.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("inputBackground", "Input Background", "Background color of the page input field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("inputForeground", "Input Foreground", "Text color of the page input field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("inputBorderColor", "Input Border Color", "Border color of the page input field.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("secondaryTextColor", "Secondary Text Color", "Color used for secondary text such as 'of 20' and ellipsis.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("iconColor", "Navigation Color", "Color used for navigation icons and labels.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);

        addProp("showNavButtonBorder", "Show Navigation Button Border", "Controls whether navigation buttons draw a border.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
        addProp("navButtonBorderColor", "Navigation Button Border Color", "Border color for navigation buttons.", CAT_APPEARANCE, PREFERRED_MASK | BOUND_MASK);
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("Pagination");
        bean.setDisplayName("Pagination");
        bean.setShortDescription("A pagination component that supports input and clickable numbers modes.");
    }

    @Override
    public Image getIcon(int kind) {
        URL url = getClass().getResource("/images/pagination_icon.png");
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