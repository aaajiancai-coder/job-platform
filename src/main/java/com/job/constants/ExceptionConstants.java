package com.job.constants;

/**
 * 异常常量类
 * 统一管理错误码、错误信息，避免硬编码
 * 错误码规则：
 * - 1xxx：通用系统异常
 * - 2xxx：认证/授权异常
 * - 3xxx：业务逻辑异常
 * - 4xxx：参数校验异常
 * - 5xxx：第三方服务异常（如AI接口、数据库）
 */
public final class ExceptionConstants {

    // ========== 私有构造方法：禁止实例化 ==========
    private ExceptionConstants() {
        throw new UnsupportedOperationException("该类为常量类，禁止实例化");
    }

    // ========== 1xxx：通用系统异常 ==========
    /** 系统未知异常 */
    public static final int SYSTEM_UNKNOWN_ERROR_CODE = 1000;
    public static final String SYSTEM_UNKNOWN_ERROR_MSG = "服务器内部错误，请稍后再试";

    /** 类型转换异常 */
    public static final int SYSTEM_CLASS_CAST_ERROR_CODE = 1001;
    public static final String SYSTEM_CLASS_CAST_ERROR_MSG = "数据类型转换失败，请联系管理员";

    /** 空指针异常 */
    public static final int SYSTEM_NULL_POINTER_ERROR_CODE = 1002;
    public static final String SYSTEM_NULL_POINTER_ERROR_MSG = "系统数据异常，请联系管理员";

    /** 接口不存在（404） */
    public static final int SYSTEM_NOT_FOUND_ERROR_CODE = 1003;
    public static final String SYSTEM_NOT_FOUND_ERROR_MSG = "请求的接口不存在";

    // ========== 2xxx：认证/授权异常 ==========
    /** 未登录/登录过期 */
    public static final int AUTH_UNAUTHORIZED_CODE = 2000;
    public static final String AUTH_UNAUTHORIZED_MSG = "用户未登录或登录已过期，请重新登录";

    /** 权限不足 */
    public static final int AUTH_FORBIDDEN_CODE = 2001;
    public static final String AUTH_FORBIDDEN_MSG = "权限不足，无法访问该资源";

    /** 匿名用户访问受保护资源 */
    public static final int AUTH_ANONYMOUS_CODE = 2002;
    public static final String AUTH_ANONYMOUS_MSG = "匿名用户禁止访问，请先登录";

    /** Token无效/解析失败 */
    public static final int AUTH_TOKEN_INVALID_CODE = 2003;
    public static final String AUTH_TOKEN_INVALID_MSG = "Token无效或已过期，请重新登录";

    // ========== 3xxx：业务逻辑异常 ==========
    /** 聊天接口 - 无可用AI回复 */
    public static final int BUSINESS_CHAT_EMPTY_CODE = 3000;
    public static final String BUSINESS_CHAT_EMPTY_MSG = "智能助手暂时无法回复，请检查网络或稍后再试";

    /** 聊天接口 - 账号禁用 */
    public static final int BUSINESS_CHAT_ACCOUNT_DISABLE_CODE = 3001;
    public static final String BUSINESS_CHAT_ACCOUNT_DISABLE_MSG = "账号已禁用，无法使用聊天功能";

    /** 用户不存在 */
    public static final int BUSINESS_USER_NOT_EXIST_CODE = 3002;
    public static final String BUSINESS_USER_NOT_EXIST_MSG = "用户不存在，请检查用户ID";

    // ========== 4xxx：参数校验异常 ==========
    /** 参数为空 */
    public static final int PARAM_EMPTY_CODE = 4000;
    public static final String PARAM_EMPTY_MSG = "请求参数不能为空";

    /** 参数格式错误 */
    public static final int PARAM_FORMAT_ERROR_CODE = 4001;
    public static final String PARAM_FORMAT_ERROR_MSG = "请求参数格式错误";

    /** 聊天接口 - 提问内容过长 */
    public static final int PARAM_CHAT_PROMPT_TOO_LONG_CODE = 4002;
    public static final String PARAM_CHAT_PROMPT_TOO_LONG_MSG = "提问内容过长，请控制在500字以内";

    // ========== 5xxx：第三方服务异常 ==========
    /** AI接口调用失败 */
    public static final int THIRD_AI_CALL_ERROR_CODE = 5000;
    public static final String THIRD_AI_CALL_ERROR_MSG = "AI服务调用失败，请稍后再试";

    /** 数据库操作失败 */
    public static final int THIRD_DB_ERROR_CODE = 5001;
    public static final String THIRD_DB_ERROR_MSG = "数据库操作失败，请联系管理员";

    // ========== SSE 异常专用常量 ==========
    /** SSE - 未登录提示 */
    public static final String SSE_UNAUTHORIZED_MSG = "您尚未登录，请先登录后再试";

    /** SSE - 系统异常提示 */
    public static final String SSE_SYSTEM_ERROR_MSG = "系统异常：%s";

    // ========== HTTP 状态码常量（适配Spring HttpStatus） ==========
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_FORBIDDEN = 403;
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
}