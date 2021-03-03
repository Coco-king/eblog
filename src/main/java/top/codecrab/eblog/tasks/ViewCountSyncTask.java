package top.codecrab.eblog.tasks;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.service.PostService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class ViewCountSyncTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PostService postService;

    /**
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void task() {
        log.info("持久化开始：【{}】================ ", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_TIME_PATTERN));
        Set<String> keys = redisTemplate.keys("post:viewCount:*");
        //如果为空则不执行
        if (CollectionUtil.isEmpty(keys)) return;
        List<String> ids = new ArrayList<>();
        //遍历
        keys.forEach(key -> {
            //截取key获得文章id
            String postId = key.substring(key.lastIndexOf(":") + 1);
            ids.add(postId);
        });
        if (CollectionUtil.isEmpty(ids)) return;

        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        List<Post> endUpdate = new ArrayList<>();
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        posts.forEach(post -> {
            String key = "post:viewCount:" + post.getId();
            log.info("缓存的key -----> {}", key);
            //取出每一个key所对应的hash，key为ip
            Map<Object, Object> map = opsForHash.entries(key);
            log.info("key对应的hash -----> {}", map);
            int count = 0;
            //取出map的value分别判断如果为1则为没有参与过访问量运算的
            for (Object k : map.keySet()) {
                //取出值
                Integer one = (Integer) map.get(k);
                //如果为1，则为参与访问量计算
                if (one == 1) count++;
                //修改map的value
                map.put(k, 2);
            }
            //count不等于0表示新增访问量了
            if (count != 0) {
                Integer viewCount = post.getViewCount();
                int total = count + viewCount;
                log.info("{} 更新之前的浏览数【{}】，更新后【{}】", post.getId(), viewCount, total);
                //新增访问量 + 数据库中的访问量 = 要持久化到数据库中的
                post.setViewCount(total);
                //将要修改访问量的文章存入集合中
                endUpdate.add(post);
                //将要修改访问量的文章更新hash的值
                opsForHash.putAll(key, map);
            }
        });
        if (!endUpdate.isEmpty()) {
            //持久化
            boolean isSuccess = postService.updateBatchById(endUpdate);
            log.info("持久化浏览数量【{}】。", isSuccess);
        }
    }
}
