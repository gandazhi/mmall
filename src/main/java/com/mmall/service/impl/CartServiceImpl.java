package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;

    private Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Override
    public ServiceResponse<CartVo> addCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServiceResponse.createByErrorMessage("没有找到传入商品id的商品");
        }
        //查找购物车中是否有这个商品
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        //cart为空，说明购物车中没有这个商品，添加商品
        if (cart == null) {
            if (count > 0) {
                Cart cartItem = new Cart();
                cartItem.setQuantity(count);
                cartItem.setProductId(productId);
                cartItem.setUserId(userId);
                cartItem.setChecked(Const.CartChecked.CHECK);
                cartMapper.insert(cartItem);
            } else {
                //cart为空，且count是负数，这种情况直接返回参数错误
                return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
        } else {
            //cart不为空，则是往购物车中添加商品数量，或减少商品数量
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo = this.getCartVoLimit(userId, count);
        return ServiceResponse.createBySuccess(cartVo);
    }

    private CartVo getCartVoLimit(Integer userId, Integer count) {
        CartVo cartVo = new CartVo();
        List<Cart> cartVoList = cartMapper.selectCartByUserId(userId);

        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //初始化购物车的总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (count >= 0) {
            //传来的count >= 0 ，对购物车中商品的数量进行处理
            for (Cart cartItem : cartVoList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());//最开始就判断了product会不会是空的情况，所以在这里不用判断product是不是空
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductSubTitle(product.getSubtitle());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductImage(product.getMainImage());
                //验证数量是否超过了库存
                int buyLimitCount = 0;


                if (product.getStock() >= cartItem.getQuantity()) {
                    //购物车中传的数量是<=product表中的库存，限制就是成功的
                    buyLimitCount = cartItem.getQuantity();
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setLimitQuantity(Const.LIMT.LIMIT_NUM_SUCCESS);
                } else {
                    //购物车中传的数量>product表中的库存,限制失败
                    buyLimitCount = product.getStock();
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setLimitQuantity(Const.LIMT.LIMIT_NUM_FAIL);

                    //再次更新cart表中的quantity数量
                    Cart cartForQuantity = new Cart();
                    cartForQuantity.setId(cartItem.getId());
                    cartForQuantity.setQuantity(buyLimitCount);
                    cartMapper.updateByPrimaryKeySelective(cartForQuantity);

                }
                //计算单个商品的总价
                BigDecimal productTotalPrice = BigDecimalUtil.mul(product.getPrice().doubleValue(), buyLimitCount);
                cartProductVo.setProductTotalPrice(productTotalPrice);
                cartProductVo.setProductChecked(cartItem.getChecked());
                //计算整个购物车的总价
                if (cartItem.getChecked() == Const.CartChecked.CHECK) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }

                cartProductVoList.add(cartProductVo);
            }
        } else {
            //传来的count<0，对购物车中商品的数量进行处理
            for (Cart cartItem : cartVoList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductSubTitle(product.getSubtitle());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductImage(product.getMainImage());

                int buyLimitCount = 0;
                if (cartItem.getQuantity() <= 0) {
                    //直接把购物车中的数量改为0
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setLimitQuantity(Const.LIMT.LIMIT_NUM_FAIL);

                    //在数据库中删除这条数据
                    int resultCount = cartMapper.deleteByPrimaryKey(cartProductVo.getId());
                    if (resultCount > 0) {
                        logger.info("购物车中的数据为0，删除了id为" + cartProductVo.getId() + "的数据");
                    } else {
                        logger.error("购物车中的数据删除失败");
                    }
                } else {
                    buyLimitCount = cartItem.getQuantity();
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setLimitQuantity(Const.LIMT.LIMIT_NUM_SUCCESS);
                }

                //计算单个商品的总价
                BigDecimal productTotalPrice = BigDecimalUtil.mul(product.getPrice().doubleValue(), buyLimitCount);
                cartProductVo.setProductTotalPrice(productTotalPrice);
                cartProductVo.setProductChecked(cartItem.getChecked());
                //计算整个购物车的总价
                if (cartItem.getChecked() == Const.CartChecked.CHECK) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
                for (int i = 0; i < cartProductVoList.size(); i++) {
                    if (cartProductVoList.get(i).getQuantity() == 0) {
                        cartProductVoList.remove(i);
                    }
                }
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setIsAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperties("qiniu.url"));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        int resultCount = cartMapper.selectCartProductCheckedByUserId(userId);
        if (resultCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ServiceResponse<CartVo> getCart(Integer userId) {
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        if (cartList.size() == 0){
            return ServiceResponse.createBySuccessMesage("购物车为空");
        }else {
            CartVo cartVo = this.getCartVoLimit(userId, 0);
            return ServiceResponse.createBySuccess(cartVo);
        }
    }

    @Override
    public ServiceResponse<CartVo> addCart(HttpServletRequest request, HttpServletResponse response, Integer productId, Integer count) {
        //用户没有登录的情况下，购物车信息存在cookie中
        if (productId == null || count == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //判断传来的productId在数据库中是不是有这个商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServiceResponse.createByErrorMessage("没有找到传入商品id的商品");
        }
        //TODO 从cookie中取出，查找cookie中是否有这个商品，判段cookie中是否有商品信息

        //TODO cookie中没有，就直接添加到cookie中
        return null;
    }
}
