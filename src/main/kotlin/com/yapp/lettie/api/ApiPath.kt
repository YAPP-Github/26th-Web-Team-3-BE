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
    TIME_CAPSULE_CREATE("/api/v1/capsule", HttpMethod.POST, AuthType.REQUIRED),
    TIME_CAPSULE_JOIN("/api/v1/capsule/{capsuleId}/join", HttpMethod.POST, AuthType.REQUIRED),
    TIME_CAPSULE_LIKE("/api/v1/capsule/{capsuleId}/like", HttpMethod.POST, AuthType.REQUIRED),
    TIME_CAPSULE_DETAIL("/api/v1/capsule/{capsuleId}", HttpMethod.GET, AuthType.OPTIONAL),

    // 파일 관련 API
    FILE_PRESIGNED_UPLOAD("/api/v1/files/presigned-url", HttpMethod.GET, AuthType.REQUIRED),
    FILE_PRESIGNED_GET("/api/v1/files/{file-id}/presigned-url", HttpMethod.GET, AuthType.NONE),

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
    PROMETHEUS_EXPORTER("/api/metric/*/*", HttpMethod.GET, AuthType.NONE),
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
                    .replace(Regex("\\{[^}]+}"), "[^/]+")
                    .replace(".", "\\.") // 리터럴 점 처리
                    .replace("/**", "(/.*)?") // 모든 depth 대응
                    .replace("**", ".*")
                    .replace("/*", "/[^/]*") // 1 depth 대응
                    .replace("*", "[^/]*") // 경로 내부 와일드카드 대응

            return actualPath.matches(Regex("^$regexPattern$"))
        }
    }
}
