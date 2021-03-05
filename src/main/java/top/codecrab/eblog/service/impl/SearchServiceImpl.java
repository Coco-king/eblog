package top.codecrab.eblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import top.codecrab.eblog.search.model.PostDocument;
import top.codecrab.eblog.search.repository.PostRepository;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.service.SearchService;
import top.codecrab.eblog.vo.PostVo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Override
    public IPage<PostDocument> search(PageRequest pageable, String q, Boolean recommend, String order) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //指定查询那几个域
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(q, "title", "authorName", "categoryName");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery("status").gte(1));
        if (recommend != null) {
            boolQueryBuilder.must(query).must(QueryBuilders.matchQuery("recommend", recommend));
        } else {
            //添加MultiMatchQueryBuilder
            boolQueryBuilder.must(query);
        }
        queryBuilder.withQuery(boolQueryBuilder);
        //判断根据什么字段排序
        if (StringUtils.isBlank(order)) {
            queryBuilder.withSort(SortBuilders.fieldSort("created").order(SortOrder.DESC));
        } else {
            queryBuilder.withSort(SortBuilders.fieldSort(order).order(SortOrder.DESC));
        }
        //添加MultiMatchQueryBuilder
        //queryBuilder.withQuery(query);
        //添加分页
        queryBuilder.withPageable(pageable);
        //执行查询
        org.springframework.data.domain.Page<PostDocument> documents = postRepository.search(queryBuilder.build());
        //把结果转为mp的分页对象，方便前端调用
        IPage<PostDocument> page =
                new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize(), documents.getTotalElements());
        page.setRecords(documents.getContent());
        return page;
    }

    @Override
    public long initES() {
        int curPage = 1;
        int pageSize = 100;
        long initTotal = 0;

        while (pageSize == 100) {
            IPage<PostVo> page = postService.paging(new Page<>(curPage, 100), null, null, null, null, null);
            List<PostVo> records = page.getRecords();

            List<PostDocument> list = records.stream().map(postVo -> {
                PostDocument document = new PostDocument();
                BeanUtil.copyProperties(postVo, document);
                return document;
            }).collect(Collectors.toList());

            postRepository.saveAll(list);
            pageSize = records.size();
            initTotal += pageSize;
        }
        return initTotal;
    }
}
