package ru.trx.vaadindemo.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.trx.vaadindemo.view.channel.ChannelListView;

public class MainLayout extends AppLayout {
    private final H2 title;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);

        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.setTooltipText("Menu toggle");

        title = new H2();
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE,
                LumoUtility.Flex.GROW);


        Header header = new Header(toggle, title);
        header.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.Padding.End.MEDIUM, LumoUtility.Width.FULL);

        addToNavbar(false, header);

        Span appName = new Span("Demo chat");
        appName.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX,
                LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.Height.XLARGE, LumoUtility.Padding.Horizontal.MEDIUM);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Lobby", ChannelListView.class, VaadinIcon.BUILDING.create()));

        Scroller scroller = new Scroller(nav);

        addToDrawer(appName, scroller);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        // set up title
        Component content = getContent();

        String titleText = "";

        if (content instanceof HasDynamicTitle titleHolder) {
            titleText = titleHolder.getPageTitle();
        } else if (content != null) {
            PageTitle pageTitleAnnotation = content.getClass().getAnnotation(PageTitle.class);
            titleText = pageTitleAnnotation != null ? pageTitleAnnotation.value() : "";
        }

        title.setText(titleText);
    }
}
