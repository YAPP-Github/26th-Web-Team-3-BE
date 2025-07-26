package com.yapp.lettie.domain.timecapsule.entity

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import com.yapp.lettie.domain.user.entity.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "time_capsule")
class TimeCapsule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User,
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
    var closedAt: LocalDateTime,
    @OneToMany(mappedBy = "timeCapsule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var timeCapsuleUsers: MutableList<TimeCapsuleUser> = mutableListOf(),
    @OneToMany(mappedBy = "timeCapsule", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var timeCapsuleLikes: MutableList<TimeCapsuleLike> = mutableListOf(),
) : BaseEntity() {
    companion object {
        fun of(
            creator: User,
            inviteCode: String,
            payload: CreateTimeCapsulePayload,
        ): TimeCapsule =
            TimeCapsule(
                creator = creator,
                inviteCode = inviteCode,
                title = payload.title,
                subtitle = payload.subtitle,
                accessType = payload.accessType,
                openAt = payload.openAt,
                closedAt = payload.closedAt,
            )
    }

    fun addUser(tcu: TimeCapsuleUser) {
        this.timeCapsuleUsers.add(tcu)
    }

    fun getStatus(now: LocalDateTime): TimeCapsuleStatus =
        when {
            now.isBefore(closedAt) -> TimeCapsuleStatus.WRITABLE
            now.isBefore(openAt) -> TimeCapsuleStatus.WAITING_OPEN
            else -> TimeCapsuleStatus.OPENED
        }

    fun isOpen(now: LocalDateTime): Boolean = now.isAfter(openAt)

    fun isNotOpen(now: LocalDateTime): Boolean = !isOpen(now)

    fun isClosed(now: LocalDateTime): Boolean = now.isAfter(closedAt)

    fun isPrivate(): Boolean = accessType == AccessType.PRIVATE
}
