package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 通过productId获取商品详情
     *
     * @param productId 要获取详情的商品id
     * @return
     */
    @RequestMapping(value = "get_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<ProductDetailVo> getDetail(Integer productId) {
        return iProductService.productDetail(productId);
    }

    /**
     * 获取商品列表页
     *
     * @param pageNum  当前分页的页数
     * @param pageSize 每页显示的数量
     * @return
     */
    @RequestMapping(value = "get_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<PageInfo> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iProductService.getProductList(pageNum, pageSize);
    }

    /**
     *
     * @param keywords 搜索关键字
     * @param pageNum 搜索列表的当前页数
     * @param pageSize 搜索列表每页显示的数量
     * @return
     */
    @RequestMapping(value = "search_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<PageInfo> searchProductList(String keywords, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                       @RequestParam(value = "orderBy", required = false) String orderBy) {
        return iProductService.searchProductList(keywords, pageNum, pageSize, orderBy);
    }
}
