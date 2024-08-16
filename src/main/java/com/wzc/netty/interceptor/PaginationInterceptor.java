package com.wzc.netty.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzc.netty.context.PaginationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.wzc.netty.constant.CommonConstant.*;


@Component
public class PaginationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String pageNum = request.getParameter(PAGE_NUM);
        String pageSize = Optional
                .ofNullable(request.getParameter(PAGE_SIZE))
                .orElse(DEFAULT_PAGE_SIZE);
        if (StrUtil.isNotBlank(pageNum) && StrUtil.isNotBlank(pageSize)) {
            PaginationContext.setPage(new Page<>(Long.parseLong(pageNum), Long.parseLong(pageSize)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PaginationContext.remove();
    }

}