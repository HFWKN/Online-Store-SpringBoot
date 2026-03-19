package com.liubingqi.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 *
 * @param <T> 数据类型
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应
    public static <T> Result<T> success() {
        return new Result<>(true, 200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, 200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, 200, message, data);
    }

    // 失败响应
    public static <T> Result<T> error() {
        return new Result<>(false, 500, "操作失败", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, 500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    // 业务异常响应
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, 400, message, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(false, code, message, null);
    }

    // 未授权响应
    public static <T> Result<T> unauthorized() {
        return new Result<>(false, 401, "未授权访问", null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(false, 401, message, null);
    }

    // 禁止访问响应
    public static <T> Result<T> forbidden() {
        return new Result<>(false, 403, "禁止访问", null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(false, 403, message, null);
    }

    // 资源不存在响应
    public static <T> Result<T> notFound() {
        return new Result<>(false, 404, "资源不存在", null);
    }

    public static <T> Result<T> notFound(String message) {
        return new Result<>(false, 404, message, null);
    }
}