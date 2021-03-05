package top.codecrab.eblog.search.model;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName = "post", shards = 5, replicas = 1)
public class PostDocument implements Serializable {

    @Id
    private Long id;
    @Field(type = FieldType.Text, store = true, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Long)
    private Long authorId;
    @Field(type = FieldType.Keyword)
    private String authorName;
    @Field(type = FieldType.Keyword, index = false)
    private String authorAvatar;

    @Field(type = FieldType.Integer, index = false)
    private Integer viewCount;
    @Field(type = FieldType.Integer, index = false)
    private Integer commentCount;
    @Field(type = FieldType.Boolean)
    private Boolean recommend;
    @Field(type = FieldType.Integer)
    private Integer level;

    @Field(type = FieldType.Long)
    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;
    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute)
    private Date created;
}
