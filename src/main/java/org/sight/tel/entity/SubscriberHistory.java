package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "subscribers_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriberHistory {

  @Column(nullable = false, updatable = false)
  private final LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "date", nullable = false, columnDefinition = "date")
  private LocalDate date;

  @Column(nullable = false)
  private Integer subscriberCount;

  // ✅ 팩토리 메서드 (channelName, channelUrl 제거)
  public static SubscriberHistory create(Channel channel, LocalDate date, int subscriberCount) {
    SubscriberHistory history = new SubscriberHistory();
    history.channel = channel;
    history.date = date;
    history.subscriberCount = subscriberCount;
    return history;
  }

  public void updateSubscriberCount(int newCount) {
    this.subscriberCount = newCount;
  }

  public void assignToChannel(Channel channel) {
    this.channel = channel;
  }

  public void unassignChannel() {
    this.channel = null;
  }
}
