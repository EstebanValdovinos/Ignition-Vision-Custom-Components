package com.inductiveautomation.ignition.examples.ce;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.examples.ce.components.input.IOSButtonComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.IOSToggleSwitch;
import com.inductiveautomation.ignition.examples.ce.components.display.StatusIndicatorComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.SlideToConfirmComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.TeslaLoadingSpinnerComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.ThermometerIndicatorComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.PieDonutChartComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.CardComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.MessageLogCardComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.LineChartComponent;
import com.inductiveautomation.ignition.examples.ce.components.display.BarChartComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.SegmentedControlComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.DropdownButtonComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.DateRangePickerComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.PopupMenuButtonComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.PaginationComponent;
import com.inductiveautomation.ignition.examples.ce.components.input.ExpandableSearchBoxComponent;
import com.inductiveautomation.vision.api.designer.VisionDesignerInterface;
import com.inductiveautomation.vision.api.designer.palette.JavaBeanPaletteItem;
import com.inductiveautomation.vision.api.designer.palette.Palette;
import com.inductiveautomation.vision.api.designer.palette.PaletteItemGroup;

/**
 * This is the Designer-scope module hook for the component example module for the Ignition SDK.
 */
public class MyModuleDesignerHook extends AbstractDesignerModuleHook {

    public static final String MODULE_ID = "custom-HMI-components-v1";

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        // Add the BeanInfo package to the search path
        context.addBeanInfoSearchPath("com.inductiveautomation.ignition.examples.ce.beaninfos");
        context.addBeanInfoSearchPath("com.inductiveautomation.ignition.examples.ce.beaninfos.input");
        context.addBeanInfoSearchPath("com.inductiveautomation.ignition.examples.ce.beaninfos.display");


        // Add my components to the palette
        VisionDesignerInterface sdk = (VisionDesignerInterface) context
                .getModule(VisionDesignerInterface.VISION_MODULE_ID);
        if (sdk != null) {
            Palette palette = sdk.getPalette();

            PaletteItemGroup group = palette.addGroup("Custom HMI Components ");
            group.addPaletteItem(new JavaBeanPaletteItem(IOSToggleSwitch.class));
            group.addPaletteItem(new JavaBeanPaletteItem(IOSButtonComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(StatusIndicatorComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(SlideToConfirmComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(TeslaLoadingSpinnerComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(ThermometerIndicatorComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(PieDonutChartComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(SegmentedControlComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(DropdownButtonComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(DateRangePickerComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(PopupMenuButtonComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(CardComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(PaginationComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(ExpandableSearchBoxComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(MessageLogCardComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(LineChartComponent.class));
            group.addPaletteItem(new JavaBeanPaletteItem(BarChartComponent.class));
        }
    }
}