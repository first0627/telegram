package org.sight.tel.repository;

import java.util.List;
import java.util.Optional;
import org.sight.tel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

  @Query("SELECT MAX(c.channelOrder) FROM Channel c")
  Optional<Integer> findMaxChannelOrder();

  List<Channel> findAllByOrderByChannelOrderAsc();
}
