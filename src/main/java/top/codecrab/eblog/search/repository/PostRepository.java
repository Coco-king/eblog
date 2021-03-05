package top.codecrab.eblog.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.codecrab.eblog.search.model.PostDocument;

public interface PostRepository extends ElasticsearchRepository<PostDocument, Long> {

}
