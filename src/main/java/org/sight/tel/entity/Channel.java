package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자 (외부에서는 생성 불가)
public class Channel {

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<SubscriberHistory> subscriberHistories = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String urlId;

  private LocalDate createdAt;

  private Integer channelOrder;

  // ✅ 팩토리 메서드
  public static Channel createChannel(String name, String urlId, int channelOrder) {
    Channel channel = new Channel();
    channel.name = name;
    channel.urlId = urlId;
    channel.createdAt = LocalDate.now(ZoneId.of("Asia/Seoul"));
    channel.channelOrder = channelOrder;
    return channel;
  }

  public void changeChannelOrder(int newOrder) {
    this.channelOrder = newOrder;
  }

  public String getChannelUrl() {
    return "https://t.me/" + urlId;
  }

  public void addSubscriberHistory(SubscriberHistory history) {
    subscriberHistories.add(history);
    history.assignToChannel(this);
  }

  public void removeSubscriberHistory(SubscriberHistory history) {
    subscriberHistories.remove(history);
    history.unassignChannel();
  }

  public List<SubscriberHistory> getSubscriberHistories() {
    return Collections.unmodifiableList(subscriberHistories);
  }
}
