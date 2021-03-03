package top.codecrab.eblog.template;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.common.templates.DirectiveHandler;
import top.codecrab.eblog.common.templates.TemplateDirective;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.vo.PostVo;

/**
 * 博客查询自定义标签
 */
@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    private PostService postService;

    @Override
    public String getName() {
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Long categoryId = handler.getLong("categoryId");
        Integer cp = handler.getInteger("cp", 1);
        Integer ps = handler.getInteger("ps", 10);
        Integer level = handler.getInteger("level");
        Boolean recommend = handler.getBoolean("recommend");
        String order = handler.getString("order");

        IPage<PostVo> page = postService.paging(new Page<>(cp, ps), categoryId, null, level, recommend, order);
        handler.put(RESULTS, page).render();
    }
}
