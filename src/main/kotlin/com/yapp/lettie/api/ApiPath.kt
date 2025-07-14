package com.yapp.lettie.api

import com.yapp.lettie.domain.auth.AuthType
import com.yapp.lettie.domain.auth.HttpMethod

enum class ApiPath(
    val path: String,
    val method: HttpMethod,
    val authType: AuthType,
) {
    // 인증 관련 API (로그인 불필요)
    OAUTH_KAKAO("/api/v1/auth/oauth/kakao", HttpMethod.GET, AuthType.NONE),
    OAUTH_KAKAO_LOGIN("/api/v1/auth/code/kakao", HttpMethod.POST, AuthType.NONE),
    OAUTH_GOOGLE("/api/v1/auth/oauth/google", HttpMethod.GET, AuthType.NONE),
    OAUTH_GOOGLE_LOGIN("/api/v1/auth/code/google", HttpMethod.POST, AuthType.NONE),
    OAUTH_NAVER("/api/v1/auth/oauth/naver", HttpMethod.GET, AuthType.NONE),
    OAUTH_NAVER_LOGIN("/api/v1/auth/code/naver", HttpMethod.POST, AuthType.NONE),

    // 타임캡슐 관련 API
    TIME_CAPSULE_LIST("/api/timecapsules", HttpMethod.GET, AuthType.REQUIRED),
    TIME_CAPSULE_CREATE("/api/timecapsules", HttpMethod.POST, AuthType.REQUIRED),
    TIME_CAPSULE_GET("/api/timecapsules/{post-id}", HttpMethod.GET, AuthType.OPTIONAL),
    TIME_CAPSULE_UPDATE("/api/timecapsules/{post-id}", HttpMethod.PUT, AuthType.REQUIRED),
    TIME_CAPSULE_DELETE("/api/timecapsules/{post-id}", HttpMethod.DELETE, AuthType.REQUIRED),

    // Static 리소스 (로그인 불필요)
    STATIC_CSS("/css/*", HttpMethod.GET, AuthType.NONE),
    STATIC_JS("/js/*", HttpMethod.GET, AuthType.NONE),
    STATIC_IMAGES("/images/*", HttpMethod.GET, AuthType.NONE),
    STATIC_FONTS("/fonts/*", HttpMethod.GET, AuthType.NONE),
    STATIC_ASSETS("/assets/*", HttpMethod.GET, AuthType.NONE),
    STATIC_FAVICON("/favicon.ico", HttpMethod.GET, AuthType.NONE),
    STATIC_ROOT("/static/*", HttpMethod.GET, AuthType.NONE),

    // API 문서 관련 (로그인 불필요)
    SWAGGER_UI("/swagger-ui/*", HttpMethod.GET, AuthType.NONE),
    SWAGGER_UI_INDEX("/swagger-ui.html", HttpMethod.GET, AuthType.NONE),
    SWAGGER_DOCS("/v3/api-docs", HttpMethod.GET, AuthType.NONE),
    SWAGGER_DOCS_CONFIG("/v3/api-docs/*", HttpMethod.GET, AuthType.NONE),
    SWAGGER_RESOURCES("/swagger-resources/*", HttpMethod.GET, AuthType.NONE),
    SWAGGER_WEBJARS("/webjars/*", HttpMethod.GET, AuthType.NONE),

    // 공통 API
    HEALTH_CHECK("/api/health", HttpMethod.GET, AuthType.NONE),
    API_DOCS("/api/docs", HttpMethod.GET, AuthType.NONE),
    ;

    companion object {
        fun getAuthType(
            path: String,
            method: String,
        ): AuthType =
            entries
                .find {
                    it.path == path && it.method.methodName.equals(method, ignoreCase = true)
                }?.authType ?: getAuthTypeByPattern(path, method)

        private fun getAuthTypeByPattern(
            path: String,
            method: String,
        ): AuthType {
            return entries
                .find { apiPath ->
                    matchesPattern(apiPath.path, path) && apiPath.method.methodName.equals(method, ignoreCase = true)
                }?.authType ?: AuthType.REQUIRED // 기본값은 인증 필요
        }

        private fun matchesPattern(
            patternPath: String,
            actualPath: String,
        ): Boolean {
            // {} 패턴과 * 패턴을 정규식으로 변환
            val regexPattern =
                patternPath
                    .replace(Regex("\\{[^}]+}"), "[^/]+") // {변수명}을 [^/]+ (슬래시가 아닌 문자들)로 변환
                    .replace("*", ".*") // *을 .* (모든 문자)로 변환
                    .replace(".", "\\.") // 리터럴 점(.)을 이스케이프 (예: favicon.ico)

            return actualPath.matches(Regex("^$regexPattern$"))
        }
    }
}
