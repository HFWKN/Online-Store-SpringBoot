package com.liubingqi.seckill.task;

import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.domain.po.Stock;
import com.liubingqi.seckill.service.IActivityService;
import com.liubingqi.seckill.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀库存预热定时任务：
 * 每分钟扫描未来 30 分钟内即将开始的活动，按活动+商品维度预热库存到 Redis。
 */

/**
 *  以后我会把活动开始时间写死为几点，然后这个定时任务就会在活动开始前的2分钟扫描一次
 */
@Component
@RequiredArgsConstructor
public class StockWarmupTask {

    private static final int PREHEAT_MINUTES = 30;

    private final IActivityService activityService;
    private final IStockService stockService;
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 * * * * ?")
    public void preheatUpcomingActivityStock() {
        RLock lock = redissonClient.getLock(SeckillRedisKeyConstants.SECKILL_WARMUP_TASK_LOCK_KEY);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, 55, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (!locked) {
            return;
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = now.plusMinutes(PREHEAT_MINUTES);

            List<Activity> upcomingActivityList = activityService.lambdaQuery()
                    .ge(Activity::getBeginTime, now)
                    .le(Activity::getBeginTime, deadline)
                    .list();
            if (CollectionUtil.isEmpty(upcomingActivityList)) {
                return;
            }

            for (Activity activity : upcomingActivityList) {
                Long activityId = activity.getId() == null ? null : activity.getId().longValue();
                if (activityId == null) {
                    continue;
                }

                List<Stock> stockList = stockService.lambdaQuery()
                        .eq(Stock::getActivityId, activityId)
                        .list();
                if (CollectionUtil.isEmpty(stockList)) {
                    continue;
                }

                Set<Long> productIdSet = stockList.stream()
                        .map(Stock::getProductId)
                        .filter(productId -> productId != null)
                        .collect(Collectors.toSet());

                for (Long productId : productIdSet) {
                    StockDto stockDto = new StockDto();
                    stockDto.setActivityId(activityId);
                    stockDto.setProductId(productId);
                    stockService.getStockNum(stockDto);
                }
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
