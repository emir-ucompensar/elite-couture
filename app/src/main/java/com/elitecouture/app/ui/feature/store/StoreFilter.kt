package com.elitecouture.app.ui.feature.store

/**
 * Represents the category filters that can be applied to the store catalogue.
 */
data class StoreFilter(
    val gender: Gender,
    val category: Category,
) {
    companion object {
        fun all() = StoreFilter(Gender.ALL, Category.ALL)
        fun menAll() = StoreFilter(Gender.MEN, Category.ALL)
        fun menPants() = StoreFilter(Gender.MEN, Category.PANTS)
        fun menJackets() = StoreFilter(Gender.MEN, Category.JACKETS)
        fun menCoats() = StoreFilter(Gender.MEN, Category.COATS)
        fun menAccessories() = StoreFilter(Gender.MEN, Category.ACCESSORIES)
        fun womenAll() = StoreFilter(Gender.WOMEN, Category.ALL)
        fun womenSkirts() = StoreFilter(Gender.WOMEN, Category.SKIRTS)
        fun womenPants() = StoreFilter(Gender.WOMEN, Category.PANTS)
        fun womenJackets() = StoreFilter(Gender.WOMEN, Category.JACKETS)
        fun womenCoats() = StoreFilter(Gender.WOMEN, Category.COATS)
        fun womenAccessories() = StoreFilter(Gender.WOMEN, Category.ACCESSORIES)
    }
}

enum class Gender {
    ALL,
    MEN,
    WOMEN,
}

enum class Category {
    ALL,
    PANTS,
    JACKETS,
    COATS,
    SKIRTS,
    ACCESSORIES,
}
