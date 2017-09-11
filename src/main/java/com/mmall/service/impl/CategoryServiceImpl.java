package com.mmall.service.impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServiceResponse addCategory(String categoryName, Integer parentId) {
        //先判断categoryName或parentId是否为空
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServiceResponse.createByErrorMessage("参数错误");
        }

        //判断更新的分类名在该节点下是否重名
        int resultCount = categoryMapper.checkCategoryName(categoryName, parentId);
        if (resultCount > 0){
            return ServiceResponse.createByErrorMessage("该节点下已有这个分类名，请更换分类名");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);//默认新建的分类是可用状态

        resultCount = categoryMapper.insert(category);
        if (resultCount > 0){
            return ServiceResponse.createBySuccessMesage("新建分类成功");
        }
        return ServiceResponse.createByErrorMessage("新建分类失败");
    }

    @Override
    public ServiceResponse updateCategoryName(String newCategoryName, Integer categoryId) {
        //先判断newCategoryName或parentId是否为空
        if (categoryId == null || StringUtils.isBlank(newCategoryName)){
            return ServiceResponse.createByErrorMessage("参数错误");
        }

        //判断更新的分类名在该节点下是否重名
        int resultCount = categoryMapper.checkCategoryName(newCategoryName, categoryId);
        if (resultCount > 0){
            return ServiceResponse.createByErrorMessage("该节点下已有这个分类名，请更换分类名");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(newCategoryName);

        resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0){
            return ServiceResponse.createBySuccess("更新分类名成功");
        }
        return ServiceResponse.createByErrorMessage("更新分类名失败");
    }
}
