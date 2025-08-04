package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {
    private final UserService userService;

    @GetMapping("/batch")
    public APIResponse insertBatchUser() {
        // start time
        Instant startTime = Instant.now();
        System.out.println("Start time: " + startTime);
        userService.insertBatchUser();
        // end time
        Instant endTime = Instant.now();
        System.out.println("End time: " + endTime);
        return APIResponse.builder()
                .message("Insert batch user successfully")
                .code(200)
                .build();
    }

    @GetMapping("/profile")
    public CompletableFuture<APIResponse> getProfile(@RequestParam String username) {
        return userService.getProfile(username).thenApply(
                userResponse -> APIResponse.builder()
                        .message("Get profile successfully")
                        .code(200)
                        .data(userResponse)
                        .build()
        );
    }
}
