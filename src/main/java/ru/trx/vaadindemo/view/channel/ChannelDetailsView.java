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

import java.util.*;

@Route(value = "channel", layout = MainLayout.class)
public class ChannelDetailsView extends VerticalLayout
        implements HasUrlParameter<String>, HasDynamicTitle {
    private static final int HISTORY_SIZE = 20;

    // service
    private final ChatService chatService;

    // view component
    private final MessageList messageList;

    // data fields
    private String channelId;
    private String channelName;
    private final Map<String, Message> receivedMessages = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > HISTORY_SIZE;
        }
    };

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

    private void receiveMessages(List<Message> incomingMessages) {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                incomingMessages.forEach(message -> receivedMessages.put(message.getId(), message));
                messageList.setItems(receivedMessages.values().stream()
                        .map(this::mapToMessageListItem)
                        .toList());
            });
        });
    }

    private Disposable subscribe() {
        Disposable subscription = chatService.liveMessages(channelId)
                .subscribe(this::receiveMessages);

        // TODO optimize searching last message
        Iterator<String> rMessagesIter = receivedMessages.keySet().iterator();
        String lastSeenMessageId = null;
        while (rMessagesIter.hasNext()) {
            lastSeenMessageId = rMessagesIter.next();
        }

        // TODO fix ordering
        receiveMessages(chatService.messageHistory(channelId, HISTORY_SIZE, lastSeenMessageId));

        return subscription;
    }

    private MessageListItem mapToMessageListItem(Message message) {
        return new MessageListItem(message.getMessage(), message.getTimestamp(), message.getAuthor());
    }
}
