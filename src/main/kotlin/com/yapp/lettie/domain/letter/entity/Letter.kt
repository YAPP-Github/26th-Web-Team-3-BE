package com.yapp.lettie.domain.letter.entity

import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.user.entity.User
import jakarta.persistence.CascadeType
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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "letter")
class Letter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val user: User,
    @JoinColumn(name = "time_capsule_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    val timeCapsule: TimeCapsule,
    var content: String,
    @Column(name = "froms")
    var from: String?,
    @OneToMany(mappedBy = "letter", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var letterFiles: MutableList<LetterFile> = mutableListOf(),
) : BaseEntity() {
    companion object {
        fun of(
            content: String,
            from: String? = null,
            user: User,
            timeCapsule: TimeCapsule,
        ): Letter =
            Letter(
                content = content,
                from = from,
                user = user,
                timeCapsule = timeCapsule,
            )
    }

    fun addFile(file: LetterFile) {
        letterFiles.add(file)
    }

    fun isMine(userId: Long): Boolean = user.id == userId
}
