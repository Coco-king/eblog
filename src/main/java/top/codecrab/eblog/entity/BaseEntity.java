package top.codecrab.eblog.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    /**
     * id使用的推特的雪花算法，长度过长，前端会丢失精度，在这里统一转换为字符串返回给前端
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Date created;

    private Date modified;

    private Integer status;

}
