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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  public Channel(String name, String urlId) {
    this.name = name;
    this.urlId = urlId;
    this.createdAt = LocalDate.now(ZoneId.of("Asia/Seoul"));
    this.channelOrder = 0;
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
