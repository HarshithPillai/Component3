package com.tweetapp.user.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Health {

    private String serverStatus;
}
