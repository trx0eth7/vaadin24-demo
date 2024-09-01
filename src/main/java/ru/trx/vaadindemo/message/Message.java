package ru.trx.vaadindemo.message;

import lombok.Getter;
import lombok.Setter;
import ru.trx.vaadindemo.channel.Channel;

import java.time.Instant;

@Getter
@Setter
//@Entity
//@Table(name = "message")
public class Message {

//    @Id
//    @Column(name = "id", nullable = false)
    private String id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "channel_id")
    private Channel channel;

//    @Column(name = "sequence_number")
    private Long sequenceNumber;

//    @Column(name = "timestamp")
    private Instant timestamp;

//    @Column(name = "author")
    private String author;

//    @Column(name = "message")
    private String message;
}