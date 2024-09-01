package ru.trx.vaadindemo.view.channel;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import reactor.core.Disposable;
import ru.trx.vaadindemo.channel.Channel;
import ru.trx.vaadindemo.chat.ChatService;
import ru.trx.vaadindemo.message.Message;
import ru.trx.vaadindemo.view.MainLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "channel", layout = MainLayout.class)
public class ChannelDetailsView extends VerticalLayout
        implements HasUrlParameter<String>, HasDynamicTitle {

    // service
    private final ChatService chatService;

    // view component
    private final MessageList messageList;

    // data fields
    private String channelId;
    private String channelName;
    private final List<Message> receivedMessages = new ArrayList<>();

    public ChannelDetailsView(ChatService chatService) {
        this.chatService = chatService;
        this.messageList = new MessageList();

        MessageInput messageInput = new MessageInput();
        messageInput.addSubmitListener(event -> {
            String message = event.getValue();
            sendMessage(message);
        });

        // w,h = 100%
        setSizeFull();
        messageList.setSizeFull();
        messageInput.setWidthFull();

        add(messageList);
        add(messageInput);
    }

    @Override
    public String getPageTitle() {
        return channelName;
    }

    @Override
    public void setParameter(BeforeEvent event, String channelId) {
        Optional<Channel> channel = chatService.channel(channelId);

        if (channel.isEmpty()) {
            event.forwardTo(ChannelListView.class);
            return;
        }

        this.channelName = channel.get().getName();
        this.channelId = channelId;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Disposable subscription = subscribe();
        addDetachListener(event -> subscription.dispose());
    }

    private void sendMessage(String message) {
        if (!message.isBlank()) {
            chatService.postMessage(channelId, message);
        }
    }

    private void receiveMessage(List<Message> incomingMessages) {
        getUI().ifPresent(ui -> {
           ui.access(() -> {
              receivedMessages.addAll(incomingMessages);
              messageList.setItems(receivedMessages.stream()
                      .map(this::mapToMessageListItem)
                      .toList());
           });
        });
    }

    private Disposable subscribe() {
        return chatService.liveMessages(channelId)
                .subscribe(this::receiveMessage);
    }

    private MessageListItem mapToMessageListItem(Message message) {
        return new MessageListItem(message.getMessage(), message.getTimestamp(), message.getAuthor());
    }
}
