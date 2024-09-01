package ru.trx.vaadindemo.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trx.vaadindemo.channel.Channel;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
@Component
public class InMemoryMessageRepository {

    private final Map<Channel, MessageArchive> messagesArchive = new ConcurrentHashMap<>();

    public List<Message> findLatest(Channel channel, int fetchMax, @Nullable String lastSeenMessageId) {
        if (fetchMax <= 0) {
            throw new IllegalArgumentException("fetchMax must be greater than 0");
        }

        return Optional.ofNullable(messagesArchive.get(channel))
                .map(messageArchive -> messageArchive.findLatest(fetchMax, lastSeenMessageId))
                .orElse(Collections.emptyList());
    }

    public Message save(Message message) {
        return messagesArchive.computeIfAbsent(message.getChannel(), MessageArchive::new)
                .save(message);
    }

    private static class MessageArchive {
        private AtomicLong sequenceNumber = new AtomicLong(1);
        private List<Message> messages = new ArrayList<>();
        private ReadWriteLock lock = new ReentrantReadWriteLock();
        private Channel channel;

        public MessageArchive(Channel channel) {
            this.channel = channel;
        }

        public List<Message> findLatest(int fetchMax, String lastSeenMessageId) {
            lock.readLock().lock();
            try {
                int indexOfLastSeenMessage = lastSeenMessageId == null ? -1 : indexOfMessage(lastSeenMessageId);
                if (messages.size() - fetchMax > indexOfLastSeenMessage) {
                    return List.copyOf(messages.subList(messages.size() - fetchMax, messages.size()));
                } else {
                    return List.copyOf(messages.subList(indexOfLastSeenMessage + 1, messages.size()));
                }
            } finally {
                lock.readLock().unlock();
            }
        }

        private int indexOfMessage(String lastSeenMessageId) {
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);

                if(message.getId().equals(lastSeenMessageId)) {
                    return i;
                }
            }

            return -1;
        }

        public Message save(Message message) {
            lock.writeLock().lock();

            try {
                String messageId = message.getId();

                if (messageId == null) {
                    messageId = UUID.randomUUID().toString();
                }

                Message newMessage = new Message();
                newMessage.setId(messageId);
                newMessage.setSequenceNumber(sequenceNumber.getAndIncrement());
                newMessage.setAuthor(message.getAuthor());
                newMessage.setMessage(message.getMessage());
                newMessage.setTimestamp(message.getTimestamp());
                newMessage.setChannel(channel);

                messages.add(newMessage);

                return newMessage;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
