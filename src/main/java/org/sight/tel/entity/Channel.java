package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
public class Channel {

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
}
