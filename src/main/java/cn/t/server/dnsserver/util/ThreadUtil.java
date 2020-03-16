package cn.t.server.dnsserver.util;

import cn.t.server.dnsserver.constants.DnsServerMessageHandlerConfig;
import cn.t.server.dnsserver.monitor.ThreadPoolMonitor;
import cn.t.server.dnsserver.protocol.HandleMessageTask;
import cn.t.server.dnsserver.thread.MonitoredThreadFactory;
import cn.t.server.dnsserver.thread.MonitoredThreadPool;

import java.util.concurrent.*;

public class ThreadUtil {

    public static final ScheduledExecutorService scheduledExecutorService =  Executors.newScheduledThreadPool(1, new MonitoredThreadFactory(DnsServerMessageHandlerConfig.SCHEDULE_THREAD_POOL_NAME));

    public static void submitMessageHandleTask(HandleMessageTask task) {
        Socks5ThreadPoolHolder.MESSAGE_HANDLER_THREAD_POOL_EXECUTOR.submit(task);
    }

    public static void scheduleTask(Runnable runnable, int initialDelayInSeconds, int periodInSeconds) {
        scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelayInSeconds, periodInSeconds, TimeUnit.SECONDS);
    }

    private static class Socks5ThreadPoolHolder {
        private static final ThreadPoolExecutor MESSAGE_HANDLER_THREAD_POOL_EXECUTOR = new MonitoredThreadPool(
                DnsServerMessageHandlerConfig.MESSAGE_HANDLER_CORE_THREAD_COUNT,
                DnsServerMessageHandlerConfig.MESSAGE_HANDLER_MAX_THREAD_COUNT,
                DnsServerMessageHandlerConfig.MESSAGE_HANDLER_THREAD_TT,
                DnsServerMessageHandlerConfig.MESSAGE_HANDLER_THREAD_TT_TIME_UNIT,
            new ArrayBlockingQueue<>(DnsServerMessageHandlerConfig.MESSAGE_HANDLER_BLOCKING_THREAD_COUNT),
            new MonitoredThreadFactory(DnsServerMessageHandlerConfig.MESSAGE_HANDLER_THREAD_POOL_NAME),
                DnsServerMessageHandlerConfig.MESSAGE_HANDLER_THREAD_POOL_NAME
        );
        static {
            scheduleTask(new ThreadPoolMonitor(DnsServerMessageHandlerConfig.MESSAGE_HANDLER_THREAD_POOL_NAME, MESSAGE_HANDLER_THREAD_POOL_EXECUTOR), 3, 5);
        }
    }



}
