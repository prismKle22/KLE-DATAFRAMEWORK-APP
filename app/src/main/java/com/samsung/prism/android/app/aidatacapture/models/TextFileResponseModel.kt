package com.samsung.prism.android.app.aidatacapture.models

data class TextFileResponseModel(
    val error: Boolean,
    val message: String,
    val file: String
)

class TypedTextResponseModel (
        val error: Boolean,
        val message: String,
)