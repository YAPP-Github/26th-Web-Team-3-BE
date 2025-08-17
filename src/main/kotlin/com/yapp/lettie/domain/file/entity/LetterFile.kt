package com.yapp.lettie.domain.file.entity

import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.letter.entity.Letter
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
@Table(name = "letter_file")
class LetterFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "object_key", nullable = false)
    val objectKey: String,
    @JoinColumn(name = "letter_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    var letter: Letter,
) : BaseEntity() {
    companion object {
        fun of(
            objectKey: String,
            letter: Letter,
        ): LetterFile =
            LetterFile(
                objectKey = objectKey,
                letter = letter,
            )
    }
}
