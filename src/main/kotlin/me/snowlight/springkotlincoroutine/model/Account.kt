package me.snowlight.springkotlincoroutine.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ACCOUNT")
class Account(
    @Id
    var id: Long = 0,
    var balance: Long = 0,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Article(id=$id, balance='$balance, ${super.toString()})"
    }
}