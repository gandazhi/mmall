package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServiceResponse addCategory(String categoryName, Integer parentId);

    ServiceResponse updateCategoryName(String newCategoryName, Integer categoryId);

    ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServiceResponse selectCategoryAndChildrenById(Integer categoryId);
}
