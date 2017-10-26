package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.impl.RecommendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/index/")
public class HomePageController {

    @Autowired
    private RecommendServiceImpl iRecommendService;

    /**
     * 用户首页推荐产品接口
     *
     * @param session    判断用户是否登录
     * @param categoryId 推荐的类别id
     * @param num        推荐的数量
     * @return
     */
    @RequestMapping(value = "getProductRecommend.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse getProductRecommend(HttpSession session, Integer categoryId, Integer num) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            //用户没有登录，按对应楼层的categoryId进行推荐
            return iRecommendService.productRecommend(0, categoryId, num);
        } else {
            //用户登录，对应楼层的categoryId按照用户浏览记录和搜索记录进行推荐
            return iRecommendService.productRecommend(user.getId(), categoryId, num);
        }
    }
}
