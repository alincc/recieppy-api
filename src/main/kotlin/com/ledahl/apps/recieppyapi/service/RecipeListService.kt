package com.ledahl.apps.recieppyapi.service

import com.ledahl.apps.recieppyapi.exception.NotAuthorizedException
import com.ledahl.apps.recieppyapi.model.RecipeList
import com.ledahl.apps.recieppyapi.model.User
import com.ledahl.apps.recieppyapi.model.input.RecipeListInput
import com.ledahl.apps.recieppyapi.repository.RecipeListRepository
import com.ledahl.apps.recieppyapi.repository.RecipeRepository
import graphql.GraphQLException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RecipeListService(@Autowired private val recipeListRepository: RecipeListRepository,
                        @Autowired private val recipeRepository: RecipeRepository) {
    fun getRecipeList(id: Long, user: User?): RecipeList? {
        user ?: throw NotAuthorizedException()
        return recipeListRepository.getRecipeList(id = id, userId = user.id)
    }

    fun getRecipeListsForUser(user: User?): List<RecipeList> {
        user ?: throw NotAuthorizedException()
        return recipeListRepository.getRecipeLists(user.id)
    }

    fun createRecipeList(recipeList: RecipeListInput, user: User?): RecipeList? {
        user ?: throw NotAuthorizedException()
        val newRecipeList = RecipeList(name = recipeList.name, created = LocalDate.now())

        val newRecipeListId = recipeListRepository.save(newRecipeList)
        if (newRecipeListId != 0) {
            recipeListRepository.saveRecipeList(recipeListId = newRecipeListId.toLong(), userId = user.id)
            return newRecipeList.copy(id = newRecipeListId.toLong())
        }
        return null
    }

    fun deleteRecipeList(id: Long, user: User?): Long {
        user ?: throw NotAuthorizedException()

        val deleted = recipeListRepository.deleteUserRecipeList(recipeListId = id, userId = user.id)
        if (deleted == 0) {
            throw GraphQLException("Recipe list (id: $id) not found")
        }

        recipeRepository.deleteRecipesForRecipeList(recipeListId = id)
        recipeListRepository.delete(recipeListId = id)
        return id
    }
}