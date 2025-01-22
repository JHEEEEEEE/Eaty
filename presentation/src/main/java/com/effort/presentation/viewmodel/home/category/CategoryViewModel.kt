package com.effort.presentation.viewmodel.home.category

import androidx.lifecycle.ViewModel
import com.effort.presentation.model.category.CategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {

    // 카테고리 목록 관리
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories get() = _categories.asStateFlow()

    fun setCategories(categoryList: List<CategoryModel>) {
        _categories.value = categoryList
    }
}