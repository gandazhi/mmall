package com.mmall.pojo;

public class UserHistory {
    private Integer id;

    private Integer userId;

    private Integer categoryId;

    private Integer productId;

    private Integer quantity;

    public UserHistory(Integer id, Integer userId, Integer categoryId, Integer productId, Integer quantity) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public UserHistory() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}