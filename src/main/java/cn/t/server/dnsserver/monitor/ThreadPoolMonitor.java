package cn.t.server.dnsserver.monitor;

import cn.t.server.dnsserver.constants.MonitorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolMonitor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MonitorConstants.THREAD_POOL_MONITOR_LOG_NAME);

    private String poolName;
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void run() {
        logger.info("{}-monitor: " +
                " 当前线程数量: {}, 核心线程数量: {}, 正在执行任务的线程数量: {}, " +
                "已完成任务数量: {}, 任务总数: {}, 队列里缓存的任务数量: {}, 池中存在的最大线程数: {}, " +
                "最大允许的线程数: {},  线程空闲时间: {}, 线程池是否关闭: {}, 线程池是否终止: {}",
            this.poolName,
            threadPoolExecutor.getPoolSize(), threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getActiveCount(),
            threadPoolExecutor.getCompletedTaskCount(), threadPoolExecutor.getTaskCount(), threadPoolExecutor.getQueue().size(), threadPoolExecutor.getLargestPoolSize(),
            threadPoolExecutor.getMaximumPoolSize(), threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS), threadPoolExecutor.isShutdown(), threadPoolExecutor.isTerminated());
    }

    public ThreadPoolMonitor(String poolName, ThreadPoolExecutor threadPoolExecutor) {
        this.poolName = poolName;
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
