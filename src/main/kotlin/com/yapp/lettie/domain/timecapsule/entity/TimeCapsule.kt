package com.yapp.lettie.domain.timecapsule.entity

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "time_capsule")
class TimeCapsule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "invite_code", nullable = false, unique = true)
    var inviteCode: String,
    @Column(name = "title", nullable = false)
    var title: String,
    @Column(name = "subtitle")
    var subtitle: String? = null,
    @Column(name = "access_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var accessType: AccessType,
    @Column(name = "open_at", nullable = false)
    val openAt: LocalDateTime,
    @Column(name = "closed_at", nullable = false)
    var closedAt: LocalDate,
    @OneToMany(mappedBy = "timeCapsule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var timeCapsuleUsers: MutableList<TimeCapsuleUser> = mutableListOf(),
    @OneToMany(mappedBy = "timeCapsule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var timeCapsuleLikes: MutableList<TimeCapsuleLike> = mutableListOf(),
) : BaseEntity() {
    companion object {
        fun of(
            inviteCode: String,
            payload: CreateTimeCapsulePayload,
        ): TimeCapsule {
            return TimeCapsule(
                inviteCode = inviteCode,
                title = payload.title,
                subtitle = payload.subtitle,
                accessType = payload.accessType,
                openAt = payload.openAt,
                closedAt = payload.closedAt,
            )
        }
    }

    fun addUser(tcu: TimeCapsuleUser) {
        this.timeCapsuleUsers.add(tcu)
    }
}
