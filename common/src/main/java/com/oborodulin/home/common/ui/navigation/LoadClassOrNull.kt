package com.oborodulin.home.common.ui.navigation

private val classMap = mutableMapOf<String, Class<*>>()

private inline fun <reified T : Any> Any.castOrNull() = this as? T
fun <T> loadClass(id: String): Class<out T>? = classMap.getOrPut(id) {
    try {
        Class.forName(id)
    } catch (e: ClassNotFoundException) {
        return null
    }
}.castOrNull()