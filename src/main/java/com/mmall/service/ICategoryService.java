package com.mmall.service;

import com.mmall.common.ServiceResponse;

public interface ICategoryService {

    ServiceResponse addCategory(String categoryName, Integer parentId);

    ServiceResponse updateCategoryName(String newCategoryName, Integer categoryId);
}
