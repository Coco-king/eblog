package top.codecrab.eblog.search.mq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.mapper.PostMapper;
import top.codecrab.eblog.search.model.PostDocument;
import top.codecrab.eblog.search.repository.PostRepository;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.vo.PostVo;

import java.util.Optional;

@Component
@Slf4j
public class PostListener {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostRepository postRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqTypes.POST_SAVE_QUEUE, durable = "true"),
            exchange = @Exchange(value = MqTypes.POST_EXCHANGE,
                    ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {MqTypes.POST_INSERT_ROUTING_KEY, MqTypes.POST_UPDATE_ROUTING_KEY}
    ))
    public void save(Long postId) {
        PostVo post = postMapper.selectOnePost(new QueryWrapper<Post>().eq("p.id", postId)
                .ge("p.status", 0));
        if (post == null) {
            log.error("mq同步保存操作时查询不到文章 postId = {}", postId);
            return;
        }
        Optional<PostDocument> optional = postRepository.findById(postId);
        PostDocument document;
        if (optional.isPresent()) {
            //更新
            document = optional.get();
        } else {
            //新增
            document = new PostDocument();
            document.setId(postId);
            document.setAuthorId(post.getAuthorId());
            document.setCreated(post.getCreated());
        }
        this.packageDocument(post, document);
        postRepository.save(document);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqTypes.POST_REMOVE_QUEUE, durable = "true"),
            exchange = @Exchange(value = MqTypes.POST_EXCHANGE,
                    ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {MqTypes.POST_REMOVE_ROUTING_KEY}
    ))
    public void remove(Long postId) {
        postRepository.deleteById(postId);
    }

    private void packageDocument(PostVo post, PostDocument document) {
        document.setTitle(post.getTitle());
        document.setAuthorName(post.getAuthorName());
        document.setAuthorAvatar(post.getAuthorAvatar());
        document.setViewCount(post.getViewCount());
        document.setCommentCount(post.getCommentCount());
        document.setRecommend(post.getRecommend());
        document.setLevel(post.getLevel());
        document.setCategoryId(post.getCategoryId());
        document.setCategoryName(post.getCategoryName());
        document.setStatus(post.getStatus());
    }
}
