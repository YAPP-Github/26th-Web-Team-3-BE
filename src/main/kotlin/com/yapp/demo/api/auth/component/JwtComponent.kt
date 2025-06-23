package com.yapp.demo.api.auth.component

import com.yapp.demo.config.JwtConfig
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

    fun verify(token: String?): Claims {
        val jwt =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .requireIssuer(jwtConfig.issuer)
                .build()
                .parseClaimsJws(token)
                .body

        return Claims(jwt)
    }

    class Claims private constructor() {
        lateinit var id: String
        lateinit var roles: Array<String>
        private lateinit var issuedAt: Date
        private lateinit var expiresAt: Date

        constructor(jwt: JwtClaims) : this() {
            this.id = jwt.subject
            this.roles = (jwt["roles"] as List<*>).map { it.toString() }.toTypedArray()
            this.issuedAt = jwt.issuedAt
            this.expiresAt = jwt.expiration
        }

        companion object {
            fun of(
                id: String,
                role: String,
            ): Claims {
                val claims = Claims()
                claims.id = id
                claims.roles = arrayOf(role)
                return claims
            }
        }
    }
}
