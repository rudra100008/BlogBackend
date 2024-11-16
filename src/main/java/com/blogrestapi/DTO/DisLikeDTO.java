package com.blogrestapi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisLikeDTO {
    private int id;
    private int userId;
    private int postId;
}
