package top.codecrab.eblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.PageRequest;
import top.codecrab.eblog.search.model.PostDocument;

public interface SearchService {
    IPage<PostDocument> search(PageRequest pageable, String q, Boolean recommend, String order);

    long initES();
}
