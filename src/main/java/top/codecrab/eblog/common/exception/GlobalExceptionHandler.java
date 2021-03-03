package top.codecrab.eblog.common.exception;

import cn.hutool.json.JSONUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import top.codecrab.eblog.common.response.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        //判断是否为ajax
        String header = request.getHeader("X-Requested-With");
        //表示这是一个Ajax请求
        if ("XMLHttpRequest".equals(header)) {
            //返回json数据
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSONUtil.toJsonStr(Result.fail(e.getMessage())));
            return null;
        }

        //web处理
        ModelAndView mv = new ModelAndView("/error/tips");
        if (e instanceof NullPointerException) {
            mv.addObject("message", "啊哦，服务器出现BUG了");
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            mv.addObject("message", "啊哦，查询出错了");
        } else {
            mv.addObject("message", e.getMessage());
        }
        return mv;
    }
}
