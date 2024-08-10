package com.wzc.netty.context;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

public class PaginationContext {

    private static final ThreadLocal<Page<?>> PAGE_CONTEXT = new ThreadLocal<>();

    public static void setPage(Page<?> page) {
        PAGE_CONTEXT.set(page);
    }

    public static Page<?> getPage() {
        Page<?> page = PAGE_CONTEXT.get();
        if (Objects.isNull(page)) {
            setPage(new Page<>());
        }
        return PAGE_CONTEXT.get();
    }

    public static Long getCurrent() {
        return getPage().getCurrent();
    }

    public static Long getSize() {
        return getPage().getSize();
    }

    public static Long getLimitCurrent() {
        return (getCurrent() - 1) * getSize();
    }

    public static void remove() {
        PAGE_CONTEXT.remove();
    }
}
