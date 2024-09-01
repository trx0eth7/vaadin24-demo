package ru.trx.vaadindemo.channel;

import lombok.Getter;
import lombok.Setter;
import ru.trx.vaadindemo.message.Message;

@Getter
@Setter
//@Entity
//@Table(name = "channel")
public class Channel {

//    @Id
//    @Column(name = "id", nullable = false)
    private String id;

//    @Column(name = "name")
    private String name;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "last_message_id")
    private Message message;
}