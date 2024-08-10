package com.wzc.netty.interceptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import com.wzc.netty.constant.CommonConstant;
import com.wzc.netty.context.PaginationContext;

import static com.wzc.netty.constant.CommonConstant.*;


@Component
public class PaginationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String pageNum = request.getParameter(PAGE_NUM);
        String pageSize = Optional
                .ofNullable(request.getParameter(PAGE_SIZE))
                .orElse(DEFAULT_PAGE_SIZE);
        // 非空、非NULL、非空白字符
        if (StringUtils.hasText(pageNum) && StringUtils.hasText(pageSize)) {
            PaginationContext.setPage(new Page<>(Long.parseLong(pageNum), Long.parseLong(pageSize)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PaginationContext.remove();
    }

}