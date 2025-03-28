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

  @Column(nullable = false)
  private final LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(nullable = false)
  private String channelName;

  @Column(nullable = false)
  private String channelUrl;

  @Column(name = "date", nullable = false, columnDefinition = "date")
  private LocalDate date;

  @Column(nullable = false)
  private Integer subscriberCount;

  public SubscriberHistory(
      String channelName, String channelUrl, LocalDate date, Integer subscriberCount) {
    this.channelName = channelName;
    this.channelUrl = channelUrl;
    this.date = date;
    this.subscriberCount = subscriberCount;
  }

  public void updateSubscriberCount(int newCount) {
    this.subscriberCount = newCount;
  }

  // 연관관계 설정: 내부에서만 사용
  public void assignToChannel(Channel channel) {
    this.channel = channel;
  }

  void unassignChannel() {
    this.channel = null;
  }
}
