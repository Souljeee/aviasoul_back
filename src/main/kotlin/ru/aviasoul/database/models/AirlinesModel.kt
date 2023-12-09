package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.Table

object AirlinesModel : Table("airlines") {
    val id = varchar("id", 100)
    val name = text("name")
    val country = varchar("country", 50)

    override val primaryKey = PrimaryKey(id)
}