package com.gaywood.stock.domain.stock.model

/**
 * The 14 major food allergens as defined by food safety regulations.
 * All food items should be labelled with applicable allergens.
 */
enum class Allergen(val displayName: String) {
    CELERY("Celery"),
    GLUTEN("Cereals containing gluten"),
    CRUSTACEANS("Crustaceans"),
    EGGS("Eggs"),
    FISH("Fish"),
    LUPIN("Lupin"),
    MILK("Milk"),
    MOLLUSCS("Molluscs"),
    MUSTARD("Mustard"),
    TREE_NUTS("Tree nuts"),
    PEANUTS("Peanuts"),
    SESAME("Sesame seeds"),
    SOYBEANS("Soybeans"),
    SULPHITES("Sulphur dioxide and sulphites")
}
