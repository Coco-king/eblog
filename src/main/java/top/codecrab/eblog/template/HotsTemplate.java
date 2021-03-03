package top.codecrab.eblog.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.common.templates.DirectiveHandler;
import top.codecrab.eblog.common.templates.TemplateDirective;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.utils.RedisUtil;

import java.util.*;

/**
 * 本周热议查询
 */
@Component
public class HotsTemplate extends TemplateDirective {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {

        //从缓存中获取本周热议
        Set<ZSetOperations.TypedTuple> tupleSet = redisUtil.getZSetRank("week:hot:post", 0, 7);
        //存储数据键值对集合
        List<Map<String, Object>> results = new ArrayList<>();
        for (ZSetOperations.TypedTuple tuple : tupleSet) {
            //取出排序集合的值和排序分数(评论数)
            Object postId = tuple.getValue();
            Double commentCount = tuple.getScore();
            //包装要返回给页面的键值对
            Map<String, Object> map = new HashMap<>();
            map.put("id", postId);
            map.put("commentCount", commentCount);
            map.put("title", redisUtil.hget("week:hot:postInfo:" + postId, "title"));
            results.add(map);
        }
        handler.put(RESULTS, results).render();
    }
}
