package org.sight.tel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelegramChannel {
    private String name;
    private String url;
    private String subscriberCount;
}
