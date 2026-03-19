package com.liubingqi.common.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统一分页结果类
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "当前页数据")
    private List<T> list;

    /**
     * 空分页结果
     */
    public static <T> PageResult<T> empty(Long total, Long pages) {
        return new PageResult<>(total, pages, Collections.emptyList());
    }

    /**
     * 空分页结果（从 Page 对象）
     */
    public static <T> PageResult<T> empty(Page<?> page) {
        return new PageResult<>(page.getTotal(), page.getPages(), Collections.emptyList());
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象转换（不转换类型）
     */
    public static <T> PageResult<T> of(Page<T> page) {
        if (page == null) {
            return new PageResult<>();
        }
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return empty(page);
        }
        return new PageResult<>(page.getTotal(), page.getPages(), page.getRecords());
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象转换（使用 Function 映射）
     */
    public static <T, R> PageResult<T> of(Page<R> page, Function<R, T> mapper) {
        if (page == null) {
            return new PageResult<>();
        }
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return empty(page);
        }
        return new PageResult<>(
                page.getTotal(),
                page.getPages(),
                page.getRecords().stream().map(mapper).collect(Collectors.toList())
        );
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象转换（使用已转换的 list）
     */
    public static <T> PageResult<T> of(Page<?> page, List<T> list) {
        return new PageResult<>(page.getTotal(), page.getPages(), list);
    }

    /**
     * 从 MyBatis-Plus 的 Page 对象转换（使用 BeanUtils 拷贝）
     */
    public static <T, R> PageResult<T> of(Page<R> page, Class<T> clazz) {
        if (page == null) {
            return new PageResult<>();
        }
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return empty(page);
        }
        List<T> list = page.getRecords().stream().map(source -> {
            try {
                T target = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                return target;
            } catch (Exception e) {
                throw new RuntimeException("对象转换失败", e);
            }
        }).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), page.getPages(), list);
    }

    /**
     * 判断是否为空
     */
    @Schema(hidden = true)
    @JsonIgnore
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }
}
