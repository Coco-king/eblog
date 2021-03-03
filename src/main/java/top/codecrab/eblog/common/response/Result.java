package top.codecrab.eblog.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int status;
    private String msg;
    private Object data;
    private String action;

    public Result(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(0, "操作成功", null);
    }

    public static Result success(Object data) {
        return new Result(0, "操作成功", data);
    }

    public static Result success(String msg, Object data) {
        return new Result(0, msg, data);
    }

    public static Result fail(String msg) {
        return new Result(-1, msg, null);
    }

    /**
     * 指定操作成功后的返回路径
     */
    public Result action(String action) {
        this.setAction(action);
        return this;
    }
}
