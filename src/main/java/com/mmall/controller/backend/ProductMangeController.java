package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.pojo.Product;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
@RequestMapping("/manage/product")
public class ProductMangeController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 更新或新建产品
     * 传product.id的是更新产品
     * 传product.id的是新建产品
     *
     * @param session 根据session判断用户是否登录，判断登录用户是否是管理员
     * @param product 传入需要更新的产品
     * @return
     */
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录的用户是管理员，继续产品更新或创建操作
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServiceResponse.createByErrorMessage("登录用户无权限进行此操作");
        }
    }

    /**
     * 更新产品销售状态
     *
     * @param session   根据session判断用户是否登录，判断用户是否是管理员
     * @param productId 待更新产品状态的产品Id
     * @param status    更新产品后的销售状态
     * @return
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员，继续更新产品销售状态操作
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServiceResponse.createByErrorMessage("用户用户无权限进行次操作");
        }
    }

    /**
     * 获取产品详情信息
     *
     * @param session   根据session来判断用户是否登录，是否是管理员
     * @param productId 传入要获取详情的产品id
     * @return
     */
    @RequestMapping(value = "get_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员
            return iProductService.manageProductDetail(productId);
        } else {
            return ServiceResponse.createByErrorMessage("用户没有权限进行此操作");
        }
    }

    /**
     * 使用pageHelper分页获取List
     *
     * @param session  判断用户是否登录，是否是管理员
     * @param pageNum  当前页数
     * @param pageSize 每页显示的数量
     * @return
     */
    @RequestMapping(value = "get_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录的用户是管理员
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("用户没有权限进行此操作");
        }
    }

    /**
     * 通过productName和productId来搜索商品，可以只传productName，或者productId，但是不能不传参数
     *
     * @param session     通过session判断用户是否登录，是否是管理员
     * @param productName 搜索的商品名
     * @param productId   搜索的商品id
     * @param pageNum     分页的当前页数
     * @param pageSize    分页，每页显示的商品数量
     * @return
     */
    @RequestMapping(value = "product_search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse productSearch(HttpSession session, String productName, Integer productId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户没有登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员
            return iProductService.productSearch(productName, productId, pageNum, pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("用户没有权限进行此操作");
        }

    }

    /**
     * 上传图片到七牛云中存储
     *
     * @param session 通过session判断用户是否登录，登录用户是否是管理员
     * @param file    上传的文件，只允许上传图片，上传到七牛云后删除本地中的图片
     * @param request 通过request来获取路径
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            //登录用户是管理员
            String path = request.getSession().getServletContext().getRealPath("upload");
            String userId = user.getId().toString();
            String targetFileName = iFileService.upload(file, path, userId);
            if (targetFileName.equals("error")) {
                return ServiceResponse.createByErrorMessage("上传图片失败");
            } else if (targetFileName.equals("error1")) {
                return ServiceResponse.createByErrorMessage("上传的文件类型不是图片");
            }
            String url = PropertiesUtil.getProperties("qiniu.url") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServiceResponse.createBySuccess(fileMap);
        } else {
            return ServiceResponse.createByErrorMessage("用户没有权限进行此操作");
        }
    }

    /**
     * simEditor 富文本中的图片上传接口
     *
     * @param session      通过session判断用户是否上传，是否是管理员
     * @param file         上传的文件
     * @param request      通过request获取到path
     * @param httpResponse 添加header
     * @return
     */
    @RequestMapping(value = "rich_text_image_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richTextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse httpResponse) {
        Map resultMap = Maps.newHashMap();
        User user = ((User) session.getAttribute(Const.CURRENT_USER));
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录，请登录");
            return resultMap;
        }
        ServiceResponse response = iUserService.checkAdminRole(user);
        if (response.isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String userId = user.getId().toString();
            String targetFileName = iFileService.upload(file, path, userId);
            if (targetFileName.equals("error1")) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传的图片类型不是图片");
                return resultMap;
            } else if (targetFileName.equals("error")) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传图片失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperties("qiniu.url") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传图片成功");
            resultMap.put("file_path", url);
            httpResponse.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "用户没有权限进行此操作");
            return resultMap;
        }
    }


}
