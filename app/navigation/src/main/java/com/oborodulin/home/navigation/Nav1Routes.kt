package com.oborodulin.home.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

private const val ROUTE_POSTS = "posts"
private const val ROUTE_POST = "posts/%s"
private const val ROUTE_USER = "users/%s"
private const val ARG_POST_ID = "postId"
private const val ARG_USER_ID = "userId"

sealed class Nav1Routes(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {

    object Posts : Nav1Routes(ROUTE_POSTS)

    object User : Nav1Routes(
        route = String.format(ROUTE_USER, "{$ARG_USER_ID}"),
        arguments = listOf(navArgument(ARG_USER_ID) {
            type = NavType.LongType
        })
    ) {

        fun routeForUser(userInput: UserInput) = String.format(ROUTE_USER, userInput.userId)


        fun fromEntry(entry: NavBackStackEntry): UserInput {
            return UserInput(entry.arguments?.getLong(ARG_USER_ID) ?: 0L)
        }
    }
}