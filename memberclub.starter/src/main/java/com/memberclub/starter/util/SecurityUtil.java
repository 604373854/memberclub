package com.memberclub.starter.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 提供当前线程安全信息的工具类，使用 {@link ThreadLocal} 保存表示已认证用户的
 * {@link SecurityInfo} 实例。
 */
public class SecurityUtil {

    private static ThreadLocal<SecurityInfo> threadLocal = new ThreadLocal<>();

    /**
     * 从线程上下文中获取当前用户的 ID。
     *
     * @return 用户标识
     */
    public static long getUserId() {
        return threadLocal.get().getUserId();
    }

    /**
     * 从 HTTP 请求中提取安全信息并放入线程上下文，请求头需包含 {@code user_id}。
     *
     * @param request 当前请求
     */
    public static void securitySet(HttpServletRequest request) {
        SecurityInfo securityInfo = new SecurityInfo();
        // 从请求头获取用户ID，解析失败会抛出 NumberFormatException
        securityInfo.setUserId(Long.parseLong(request.getHeader("user_id")));
        threadLocal.set(securityInfo);
    }

    /**
     * 清除线程上下文，防止请求结束后造成内存泄漏。
     */
    public static void clear() {
        threadLocal.remove();
    }
}
