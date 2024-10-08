package ru.trx.vaadindemo.view.channel;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import ru.trx.vaadindemo.channel.Channel;
import ru.trx.vaadindemo.chat.ChatService;
import ru.trx.vaadindemo.security.Roles;
import ru.trx.vaadindemo.view.MainLayout;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Chats")
@PermitAll
public class ChannelListView extends VerticalLayout {

    // service & beans
    private final ChatService chatService;
    private final AuthenticationContext authContext;

    // view component
    private final VirtualList<Channel> channels;
    private final TextField channelNameField;
    private final Button channelAddButton;

    public ChannelListView(ChatService chatService, AuthenticationContext authContext) {
        this.chatService = chatService;
        this.authContext = authContext;

        channels = new VirtualList<>();
        channels.setRenderer(new ComponentRenderer<>(this::createChannelComponent));

        setSizeFull();
        add(channels);
        expand(channels);

        channelNameField = new TextField();
        channelNameField.setPlaceholder("New channel name");

        channelAddButton = new Button();
        channelAddButton.setText("Add channel");
        channelAddButton.addClickListener(e -> addChannel());
        channelAddButton.setDisableOnClick(true);
        channelAddButton.addClickShortcut(Key.ENTER);
        channelAddButton.addClassName("primary");

        if (authContext.hasRole(Roles.ADMIN)) {
            HorizontalLayout toolbar = new HorizontalLayout(channelNameField, channelAddButton);
            toolbar.setWidthFull();
            toolbar.expand(channelNameField);
            add(toolbar);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        refreshChannels();
    }

    private void refreshChannels() {
        channels.setItems(chatService.channels());
    }

    private void addChannel() {
        try {
            String channelName = channelNameField.getValue();

            if (!channelName.isBlank()) {
                chatService.createChannel(channelName);
                channelNameField.clear();
                refreshChannels();
            }
        } finally {
            channelAddButton.setEnabled(true);
        }
    }

    private Component createChannelComponent(Channel channel) {
        return new RouterLink(channel.getName(), ChannelDetailsView.class, channel.getId());
    }

}
