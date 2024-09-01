package ru.trx.vaadindemo.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trx.vaadindemo.message.InMemoryMessageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryChannelRepository {

    private final InMemoryMessageRepository inMemoryMessageRepository;

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public List<Channel> findAll() {
        return channels.values().stream()
                .sorted(Comparator.comparing(Channel::getName))
                .map(this::addLatestMessageIfAvailable)
                .collect(Collectors.toList());
    }

    private Channel addLatestMessageIfAvailable(Channel channel) {
        return inMemoryMessageRepository.findLatest(channel, 1, null).stream()
                .findFirst()
                .map(msg -> {
                    Channel latestChannel = new Channel();
                    latestChannel.setId(channel.getId());
                    latestChannel.setName(channel.getName());
                    latestChannel.setMessage(msg);
                    return latestChannel;
                })
                .orElse(channel);
    }

    public Channel save(Channel channel) {
        String channelId = channel.getId();

        if (channelId == null) {
            channelId = UUID.randomUUID().toString();
        }

        channel.setId(channelId);
        channels.put(channelId, channel);

        return channel;
    }

    public Optional<Channel> findById(String channelId) {
       return Optional.ofNullable(channels.get(channelId)).map(this::addLatestMessageIfAvailable);
    }

    public boolean exists(String channelId) {
        return channels.containsKey(channelId);
    }
}
