package com.dvm.bookstore.batch;

import com.dvm.bookstore.service.ScheduledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Scope("prototype")
public class ProcessApproveThread implements Runnable {
    private final ScheduledService scheduledService;

    @Override
    public void run() {
        log.info("====RUNNING TO UPDATE BOOK STATUS====");
        scheduledService.updateBookStatus();
    }

}
