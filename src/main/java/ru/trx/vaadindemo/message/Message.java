package ru.trx.vaadindemo.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.trx.vaadindemo.channel.Channel;

import java.time.Instant;

@Getter
@Setter
//@Entity
//@Table(name = "message")
@EqualsAndHashCode
public class Message {

    //    @Id
//    @Column(name = "id", nullable = false)
    private String id;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "channel_id")
    @EqualsAndHashCode.Exclude
    private Channel channel;

    //    @Column(name = "sequence_number")
    @EqualsAndHashCode.Exclude
    private Long sequenceNumber;

    //    @Column(name = "timestamp")
    @EqualsAndHashCode.Exclude
    private Instant timestamp;

    //    @Column(name = "author")
    @EqualsAndHashCode.Exclude
    private String author;

    //    @Column(name = "message")
    @EqualsAndHashCode.Exclude
    private String message;
}