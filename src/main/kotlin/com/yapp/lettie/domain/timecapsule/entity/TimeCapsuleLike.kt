package com.yapp.lettie.domain.timecapsule.entity

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "time_capsule_like")
class TimeCapsuleLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "is_liked", nullable = false)
    var isLiked: Boolean = true,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val timeCapsule: TimeCapsule,
) : BaseEntity(){
    companion object{
        fun of(
            user: User,
            timeCapsule: TimeCapsule,
        ): TimeCapsuleLike {
            return TimeCapsuleLike(
                user = user,
                timeCapsule = timeCapsule,
            )
        }
    }
}
