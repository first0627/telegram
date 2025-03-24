package org.sight.tel.repository;

import org.sight.tel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
  Optional<Channel> findByUrlId(String urlId);

  List<Channel> findAllByOrderByChannelOrderAsc(); // 💡 순서대로 정렬해서 가져오는 메서드
}
