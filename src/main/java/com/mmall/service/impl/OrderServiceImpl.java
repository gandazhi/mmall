package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.QiniuUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static AlipayTradeService tradeService;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private CartMapper cartMapper;


    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Override
    public ServiceResponse pay(Long orderNum, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdOrderNum(orderNum, userId);
        if (order == null) {
            return ServiceResponse.createByErrorMessage("该用户没有这个订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServiceResponse.createByErrorMessage("用户这个订单已经支付了");
        }
        resultMap.put("orderNum", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("gandazhi mmall，扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNumUserId(orderNum, userId);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperties("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("/qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                QiniuUtil.uploadForQiniu(Const.Zone.EAST_CHINA, qrPath, targetFile.getName());

                String qrUrl = PropertiesUtil.getProperties("qiniu.url") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                resultMap.put("msg", "支付宝预下单成功");
                return ServiceResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServiceResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServiceResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    @Override
    public ServiceResponse aliCallBack(Map<String, String> params) {
        logger.info("正在调用aliCallBack");
        Long orderNum = Long.parseLong(params.get("out_trade_no"));
        logger.info("orderNum:" + orderNum);
        String tradeNum = params.get("trade_no");
        logger.info("tradeNum:" + tradeNum);
        String tradeStatus = params.get("trade_status");
        logger.info("tradeStatus:" + tradeStatus);
        Order order = orderMapper.selectByOrderNum(orderNum);
        if (order == null) {
            logger.error("没有找到这个订单，回调忽略");
            return ServiceResponse.createByErrorMessage("没有找到这个订单，回调忽略");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            logger.error("重复调用支付宝");
            return ServiceResponse.createBySuccessMesage("重复调用支付宝");
        }
        if (Const.AlipayCallback.TRADE_STATUS_SUCCESS.equals(tradeStatus)) {
            logger.info("正在更新订单状态");
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNum);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.updateByPrimaryKeySelective(payInfo);
        return ServiceResponse.createBySuccess();
    }

    @Override
    public ServiceResponse queryOrderPayStatus(Integer userId, Long orderNum) {
        Order order = orderMapper.selectByUserIdOrderNum(orderNum, userId);
        if (order == null) {
            return ServiceResponse.createByErrorMessage("该用户没有这个订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }

    //将下单信息保存到遍历出来
    private ServiceResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        //先判断传来的cartList是否是空，是空则返回购物车是空的
        if (CollectionUtils.isEmpty(cartList)) {
            return ServiceResponse.createByErrorMessage("购物车是空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        //校验购物车的数据，包括产品的数量和状态
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验商品是否是下架状态
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServiceResponse.createByErrorMessage("商品" + product.getName() + "现在已经下架了");
            }
            //验证商品库存是否足够
            if (cartItem.getQuantity() > product.getStock()) {
                return ServiceResponse.createByErrorMessage("商品" + product.getName() + "库存不够");
            }
            //组装orderItem
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), orderItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServiceResponse.createBySuccess(orderItemList);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        order.setOrderNo(this.generationOrderNum());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setPostage(0);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间，付款时间等等在之后进行更新
        int resultCount = orderMapper.insert(order);
        if (resultCount > 0) {
            return order;
        }
        return null;//小于0，则是没有订单没有生成成功
    }

    private long generationOrderNum() {
        long currentTime = System.currentTimeMillis();
        long orderNum = currentTime + new Random().nextInt(100);
        return orderNum;
    }

    @Override
    public ServiceResponse createOrder(Integer userId, Integer shippingId) {
        //先判断shippingId是否为空，是否存在这个id
        if (shippingId == null){
            return ServiceResponse.createByErrorMessage("shippingId不能为空");
        }
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId, shippingId);
        if (shipping == null){
            return ServiceResponse.createByErrorMessage("该用户没有这个shippingId");
        }
        //获取购物车中的商品信息List
        List<Cart> cartList = cartMapper.selectCartByUserIdChecked(userId);
        //将商品信息从购物车中遍历出来
        ServiceResponse response = this.getCartOrderItem(userId, cartList);
        if (!response.isSuccess()){
            return response;
        }
        //计算这个订单的总价
        List<OrderItem> orderItemList = ((List<OrderItem>) response.getData());
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServiceResponse.createByErrorMessage("购物车为空");
        }
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null){
            return ServiceResponse.createByErrorMessage("生成订单失败");
        }
        //在order_item表中设置订单号
        for (OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);
        //减少商品库存
        this.reduceProductStock(orderItemList);
        //清空留在购物车中购买商品的信息
        this.cleanCart(cartList);
        //给前端返回VO对象等...
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServiceResponse.createBySuccess(orderVo);
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getStatus());
        orderVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            //组装shippingVo
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperties("qiniu.url"));

        //组装orderItemVoList
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);

        return orderVo;

    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    //减少商品库存
    private void reduceProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //清空购物车信息
    private void cleanCart(List<Cart> cartList){
        for (Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

}
