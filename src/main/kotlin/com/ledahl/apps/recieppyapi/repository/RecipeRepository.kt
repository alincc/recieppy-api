package com.ledahl.apps.recieppyapi.repository

import com.ledahl.apps.recieppyapi.model.Recipe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository

@Repository
class RecipeRepository(@Autowired private val jdbcTemplate: JdbcTemplate) {
    fun getRecipes(): List<Recipe> {
        return jdbcTemplate.query("SELECT * FROM recipe") { rs, _ ->
            Recipe(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    url = rs.getString("url"),
                    imageUrl = rs.getString("image_url"),
                    site = rs.getString("site"),
                    recipeListId = rs.getLong("recipe_list_id")
            )
        }
    }

    fun getRecipe(id: Long): Recipe? {
        val namedTemplate = NamedParameterJdbcTemplate(jdbcTemplate)
        val parameterSource = MapSqlParameterSource()
        parameterSource.addValue("id", id)

        return try {
            namedTemplate.queryForObject("SELECT * FROM recipe WHERE id = :id", parameterSource) { rs, _ ->
                Recipe(
                        id = rs.getLong("id"),
                        title = rs.getString("title"),
                        url = rs.getString("url"),
                        imageUrl = rs.getString("image_url"),
                        site = rs.getString("site"),
                        recipeListId = rs.getLong("recipe_list_id")
                )
            }
        } catch (exception: DataAccessException) {
            null
        }
    }

    fun getRecipesForRecipeList(id: Long): List<Recipe> {
        val namedTemplate = NamedParameterJdbcTemplate(jdbcTemplate)
        val parameterSource = MapSqlParameterSource()
        parameterSource.addValue("id", id)

        return namedTemplate.query(
                "SELECT r.id, r.title, r.url, r.image_url, r.site " +
                        "FROM recipe_list rl " +
                        "INNER JOIN recipe r on rl.id = r.recipe_list_id " +
                        "WHERE rl.id = :id",
                parameterSource) { rs, _ ->
            Recipe(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    url = rs.getString("url"),
                    imageUrl = rs.getString("image_url"),
                    site = rs.getString("site"),
                    recipeListId = rs.getLong("recipe_list_id")
            )
        }
    }

    fun save(recipe: Recipe): Number {
        val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
                .withTableName("recipe")
                .usingGeneratedKeyColumns("id")

        val parameters = HashMap<String, Any?>()
        parameters["title"] = recipe.title
        parameters["url"] = recipe.url
        parameters["image_url"] = recipe.imageUrl
        parameters["site"] = recipe.site
        parameters["recipe_list_id"] = recipe.recipeListId

        return simpleJdbcInsert.executeAndReturnKey(MapSqlParameterSource(parameters))
    }
}