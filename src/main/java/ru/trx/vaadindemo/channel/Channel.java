package ru.trx.vaadindemo.channel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.trx.vaadindemo.message.Message;

@Getter
@Setter
@EqualsAndHashCode
//@Entity
//@Table(name = "channel")
public class Channel {

    //    @Id
//    @Column(name = "id", nullable = false)
    private String id;

    //    @Column(name = "name")
    @EqualsAndHashCode.Exclude
    private String name;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "last_message_id")
    @EqualsAndHashCode.Exclude
    private Message message;
}