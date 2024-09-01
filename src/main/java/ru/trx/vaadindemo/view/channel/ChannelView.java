package ru.trx.vaadindemo.view.channel;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import reactor.core.Disposable;
import ru.trx.vaadindemo.chat.ChatService;
import ru.trx.vaadindemo.message.Message;

import java.util.ArrayList;
import java.util.List;

@Route("channel")
public class ChannelView extends VerticalLayout
        implements HasUrlParameter<String> {

    // service
    private final ChatService chatService;

    // view component
    private final MessageList messageList;

    // data fields
    private String channelId;
    private final List<Message> receivedMessages = new ArrayList<>();

    public ChannelView(ChatService chatService) {
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
    public void setParameter(BeforeEvent event, String channelId) {
        if (chatService.channel(channelId).isEmpty()) {
            throw new IllegalArgumentException("Invalid channel id: " + channelId);
        }
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
