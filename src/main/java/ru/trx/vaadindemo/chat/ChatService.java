package ru.trx.vaadindemo.chat;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.trx.vaadindemo.channel.Channel;
import ru.trx.vaadindemo.channel.InMemoryChannelRepository;
import ru.trx.vaadindemo.message.InMemoryMessageRepository;
import ru.trx.vaadindemo.message.Message;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {
    private static final Duration BUFFER_DURATION = Duration.ofMillis(500);
    private final Sinks.Many<Message> sink = Sinks.many().multicast().directBestEffort();

    private final InMemoryChannelRepository inMemoryChannelRepository;
    private final InMemoryMessageRepository inMemoryMessageRepository;
    private final Clock clock;

    @PostConstruct
    private void afterInit() {
        String[] chatChannels = {
                "TechTalks Central",
                "Mindful Mornings",
                "Global Gourmet Guild",
                "Fitness Frontiers",
                "Bookworm Bungalow",
                "Creative Corner",
                "Eco Enthusiasts",
                "History Huddle",
                "Music Mavens",
                "Travel Trekkers",
                "Gamer's Grind",
                "Pet Parade",
                "Fashion Forward",
                "Science Sphere",
                "Artists' Alley",
                "Movie Maniacs",
                "Entrepreneur Exchange",
                "Health Hub",
                "DIY Den",
                "Language Labyrinth"
        };

        for (String channelName : chatChannels) {
            Channel channel = createChannel(channelName);
            log.info("Created channel: {} (http://localhost:8080/channel/{})", channel.getName(), channel.getId());
        }
    }

    public List<Channel> channels() {
        return inMemoryChannelRepository.findAll();
    }

    public Channel createChannel(String channelName) {
        Channel channel = new Channel();
        channel.setName(channelName);
        return inMemoryChannelRepository.save(channel);
    }

    public Optional<Channel> channel(String channelId) {
        return inMemoryChannelRepository.findById(channelId);
    }

    public Flux<List<Message>> liveMessages(String channelId) {
        if (!inMemoryChannelRepository.exists(channelId)) {
            throw new IllegalArgumentException("Channel not found: " + channelId);
        }

        return sink.asFlux().filter(m -> m.getChannel().getId().equals(channelId)).buffer(BUFFER_DURATION);
    }

    public List<Message> messageHistory(String channelId, int fetchMax, @Nullable String lastSeenMessageId) {
        // TODO fix channel
        Channel channel = new Channel();
        channel.setId(channelId);
        return inMemoryMessageRepository.findLatest(channel, fetchMax, lastSeenMessageId);
    }

    public void postMessage(String channelId, String message) {
        if (!inMemoryChannelRepository.exists(channelId)) {
            throw new IllegalArgumentException("Channel not found: " + channelId);
        }

        String author = "Alex Vasilev";
        Message copyMessage = new Message();
        Channel channel = new Channel();

        channel.setId(channelId);
        copyMessage.setAuthor(author);
        copyMessage.setTimestamp(clock.instant());
        copyMessage.setChannel(channel);
        copyMessage.setMessage(message);

        Message savedMessage = inMemoryMessageRepository.save(copyMessage);

        Sinks.EmitResult emitResult = sink.tryEmitNext(savedMessage);

        if (emitResult.isFailure()) {
            log.error("Error posting message to channel {}: {}", channelId, emitResult);
        }
    }
}
