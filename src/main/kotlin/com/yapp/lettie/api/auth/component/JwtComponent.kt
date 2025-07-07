package com.yapp.lettie.api.auth.component

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.config.JwtConfig
import io.jsonwebtoken.Claims as JwtClaims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtComponent(
    private val jwtConfig: JwtConfig,
) {
    private val key = Keys.hmacShaKeyFor(jwtConfig.clientSecret.toByteArray())

    fun create(
        userId: Long,
        role: String,
    ): String {
        val now = Date()
        val expiration = jwtConfig.getExpirationDate(now)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .setIssuer(jwtConfig.issuer)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .claim("roles", listOf(role))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun create(claims: Claims): String {
        val now = Date()
        val expiration = jwtConfig.getExpirationDate(now)

        return Jwts
            .builder()
            .setSubject(claims.id)
            .setIssuer(jwtConfig.issuer)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .claim("roles", claims.roles.toList())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun verify(token: String): Claims {
        try {
            val jwt =
                Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(jwtConfig.issuer)
                    .build()
                    .parseClaimsJws(token)
                    .body
            return Claims(jwt)
        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
            throw ApiErrorException(ErrorMessages.EXPIRED_TOKEN)
        } catch (e: io.jsonwebtoken.JwtException) {
            throw ApiErrorException(ErrorMessages.INVALID_TOKEN)
        } catch (e: Exception) {
            throw ApiErrorException(ErrorMessages.INVALID_TOKEN)
        }
    }

    class Claims private constructor(
        val id: String,
        val roles: Array<String>,
        val issuedAt: Date,
        val expiresAt: Date,
    ) {
        constructor(jwt: JwtClaims) : this(
            id = jwt.subject,
            roles = (jwt["roles"] as List<*>).map { it.toString() }.toTypedArray(),
            issuedAt = jwt.issuedAt,
            expiresAt = jwt.expiration,
        )

        companion object {
            fun of(
                id: String,
                role: String,
                now: Date,
                expiration: Date,
            ): Claims =
                Claims(
                    id = id,
                    roles = arrayOf(role),
                    issuedAt = now,
                    expiresAt = expiration,
                )
        }
    }
}
