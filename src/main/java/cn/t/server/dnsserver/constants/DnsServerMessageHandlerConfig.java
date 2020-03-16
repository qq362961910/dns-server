package cn.t.server.dnsserver.constants;

import java.util.concurrent.TimeUnit;

public class DnsServerMessageHandlerConfig {

    public static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String MESSAGE_HANDLER_THREAD_POOL_NAME = "dns-message-handler";
    public static final String SCHEDULE_THREAD_POOL_NAME = "schedule";
    public static final int MESSAGE_HANDLER_CORE_THREAD_COUNT = (PROCESSOR_COUNT < 4 ? 2 : PROCESSOR_COUNT) * 6;
    public static final int MESSAGE_HANDLER_BLOCKING_THREAD_COUNT = MESSAGE_HANDLER_CORE_THREAD_COUNT / 3;
    public static final int MESSAGE_HANDLER_MAX_THREAD_COUNT = (MESSAGE_HANDLER_CORE_THREAD_COUNT + MESSAGE_HANDLER_BLOCKING_THREAD_COUNT) * 2;
    public static final int MESSAGE_HANDLER_THREAD_TT = 10;
    public static final TimeUnit MESSAGE_HANDLER_THREAD_TT_TIME_UNIT = TimeUnit.SECONDS;

}
