package com.dvm.bookstore.scheduled;

import com.dvm.bookstore.entity.ConfigJob;
import com.dvm.bookstore.repository.ConfigJobRepository;
import com.dvm.bookstore.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
public class DynamicSchedule implements SchedulingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(DynamicSchedule.class);
    private final ApplicationContext context;
    private final JdbcTemplateLockProvider lockProvider;
    private ScheduledTaskRegistrar taskRegistrar;

    private final ConfigJobRepository configJobRepository;
    private final Map<String, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();
    private final Map<String, String> jobCache = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
        taskRegistrar.setScheduler(taskScheduler());
        refreshJob();
    }
     /**
     * Refresh jobs at a fixed delay, default is 30 seconds.
     * You can override this with the property job.dynamic.refresh.
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void refreshJob() throws InterruptedException {
        System.out.println("Refreshing jobs..." + Instant.now());
        Thread.sleep(14000);
        List<ConfigJob> activeJobs = configJobRepository.findAllByStatus(1);
        Set<String> currentJobNames = new HashSet<>();
        for(ConfigJob job : activeJobs) {
            currentJobNames.add(job.getName());
            String jobJson = DataUtils.objectToJson(job);
            if (!jobCache.containsKey(job.getName()) || !jobCache.get(job.getName()).equals(jobJson)) {
                log.info("[Scheduler] (Re)Scheduling job: {}", job.getName());
                cancelJob(job.getName());
                scheduledJob(job);
                jobCache.put(job.getName(), jobJson);
            }
        }
        // xoá job không còn tồn tại trong CSDL
        for (String oldJob : new HashSet<>(taskFutures.keySet())) {
            if (!currentJobNames.contains(oldJob)) {
                log.info("[Scheduler] Cancelling stale job: {}", oldJob);
                cancelJob(oldJob);
            }
        }
        System.out.println("Finished refreshing jobs at " + Instant.now());
    }

    private void cancelJob(String jobName) {
        ScheduledFuture<?> future = taskFutures.remove(jobName);
        if(future != null) {
            future.cancel(true);
        }
    }

    private void scheduledJob(ConfigJob job) {
        Runnable task = resolveRunnable(job);
        if (task == null) return;

        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
        Runnable lockedTask = () -> {
            Instant lockAtLeast = Instant.now().plusSeconds(job.getLockAtLeast());
            Instant lockAtMost = Instant.now().plusSeconds(job.getLockAtMost());
            executor.executeWithLock(task, new LockConfiguration(job.getName(), lockAtMost, lockAtLeast));
        };
        ScheduledFuture<?> future;
        if(isNumeric(job.getCron())) {
            long delay = Long.parseLong(job.getCron()) *1000;
            future = taskRegistrar.getScheduler().scheduleAtFixedRate(lockedTask, delay);
        } else {
            Trigger trigger = new CronTrigger(job.getCron());
            future = taskRegistrar.getScheduler().schedule(lockedTask, trigger);
        }
        taskFutures.put(job.getName(), future);
    }

    private Runnable resolveRunnable(ConfigJob job) {
        try {
            Object bean = context.getBean(job.getClassName());
            if (bean instanceof Runnable) {
                return (Runnable) bean;
            } else {
                log.error("[Scheduler] Class {} does not implement Runnable", job.getClassName());
                return null;
            }
        } catch (Exception e) {
            log.error("[Scheduler] Error resolving class {}: {}", job.getClassName(), e.getMessage(), e);
        }
        return null;
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("DynamicScheduler-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.initialize();
        return scheduler;
    }

}
