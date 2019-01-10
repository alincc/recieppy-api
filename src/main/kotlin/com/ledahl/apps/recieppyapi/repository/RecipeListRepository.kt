package com.ledahl.apps.recieppyapi.repository

import com.ledahl.apps.recieppyapi.model.RecipeList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RecipeListRepository(@Autowired private val jdbcTemplate: JdbcTemplate) {
    fun getRecipeLists(): List<RecipeList> {
        return jdbcTemplate.query("SELECT * FROM recipe_list") { rs, _ ->
            RecipeList(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    created = rs.getDate("created").toLocalDate()
            )
        }
    }

    fun getRecipeList(id: Long): RecipeList? {
        val namedTemplate = NamedParameterJdbcTemplate(jdbcTemplate)
        val parameterSource = MapSqlParameterSource()
        parameterSource.addValue("id", id)

        return try {
            namedTemplate.queryForObject("SELECT * FROM recipe_list WHERE id = :id", parameterSource) { rs, _ ->
                RecipeList(
                        id = rs.getLong("id"),
                        name = rs.getString("name"),
                        created = rs.getDate("created").toLocalDate()
                )
            }
        } catch (exception: DataAccessException) {
            null
        }
    }

    fun save(recipeList: RecipeList): Number {
        val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
                .withTableName("recipe_list")
                .usingGeneratedKeyColumns("id")

        val parameters = HashMap<String, Any>()
        parameters["name"] = recipeList.name
        parameters["created"] = Date()

        return simpleJdbcInsert.executeAndReturnKey(MapSqlParameterSource(parameters))
    }

    fun saveListToUser(recipeListId: Long, userId: Long) {
        val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_recipe_list")
                .usingGeneratedKeyColumns("id")

        val parameters = HashMap<String, Any>()
        parameters["user_id"] = userId
        parameters["recipe_list"] = recipeListId

        simpleJdbcInsert.execute(MapSqlParameterSource(parameters))
    }
}