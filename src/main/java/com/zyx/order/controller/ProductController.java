package com.zyx.order.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyx.order.entity.Category;
import com.zyx.order.entity.Product;
import com.zyx.order.entity.ProductVO;
import com.zyx.order.entity.User;
import com.zyx.order.service.CategoryService;
import com.zyx.order.service.ProductService;
import com.zyx.order.service.UserService;
import com.zyx.order.util.Page;
import com.zyx.order.util.UploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 商品模块controller
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private CategoryService categoryService;

    @RequestMapping("/list")
    public String list(Model model, Page page){
        PageHelper.offsetPage(page.getStart(),page.getCount());
        List<Product> list= productService.list();
        int total = (int) new PageInfo<>(list).getTotal();
        page.setTotal(total);

        model.addAttribute("list",list);
        model.addAttribute("total",total);
        model.addAttribute("page", page);

        return "productmodule/product-list";
    }


    @RequestMapping("/enableStatus")
    @ResponseBody
    public String enableStatus(@RequestParam(value = "name") String name){
        return productService.enableStatus(name);
    }

    @RequestMapping("/stopStatus")
    @ResponseBody
    public String stopStatus(@RequestParam(value = "name") String name){
        return productService.stopStatus(name);
    }

    @RequestMapping("/productAddUI")
    public String addUI(Model model){

        List<Category> categoryList = categoryService.list();

        List<User> userList = userService.list();

        model.addAttribute("categoryList",categoryList);
        model.addAttribute("userList",userList);

        return "productmodule/product-add";
    }

    @RequestMapping("/addProduct")
    public String add(Product product, HttpSession session, UploadUtil upload) throws IOException {

        productService.save(product);
        if (upload != null) {
            String imageName = product.getId()+".jpg";

            File file = new File(session.getServletContext().getRealPath("/images/product"),imageName);

            System.out.println(session.getServletContext().getRealPath("/images/product"));

            file.getParentFile().mkdirs();
            upload.getImage().transferTo(file);

            System.out.println("["+product.getId()+","+"images/product/"+imageName+"]");

            ProductVO vo = new ProductVO();
            vo.setId(product.getId());
            vo.setImageUrl("images/product/"+imageName);

            productService.setImageURL(vo);

            System.out.println(productService.get(product.getId()));
        }
        return "redirect:list";
    }

    @RequestMapping("/deleteProduct")
    public String del(@RequestParam(value = "id")int id,HttpSession session){
        productService.del(id);
        String imageName = id+".jpg";
        File file = new File(session.getServletContext().getRealPath("/images/product"),imageName);
        file.delete();
        return "redirect:list";
    }

    @RequestMapping("/editProduct")
    public String editUI(@RequestParam(value = "id")int id,Model model){
        //获得要修改商品的信息
        Product product = productService.get(id);
        model.addAttribute("product",product);
        System.out.println(product);

        List<Category> categoryList = categoryService.list();
        List<User> userList = userService.list();
        //通过商品id 返回所属分类
        Category categoryByid = productService.getCategoryByCid(id);
        model.addAttribute("crrentCategory",categoryByid);
        //通过id返回所属商家
        User userById = userService.getUserByPid(id);
        model.addAttribute("crrentUser",userById);

        model.addAttribute("categoryList",categoryList);
        model.addAttribute("userList",userList);

        return "productmodule/product-edit";
    }

    @RequestMapping("/updateProduct")
    public String update(Product product, HttpSession session, UploadUtil upload) throws IOException {
        productService.update(product);
        if(upload!=null){

            String imageName = product.getId()+".jpg";

            File file = new File(session.getServletContext().getRealPath("/images/product"),imageName);

            file.getParentFile().mkdirs();
            upload.getImage().transferTo(file);

            ProductVO vo = new ProductVO();
            vo.setId(product.getId());
            vo.setImageUrl("images/product/"+imageName);

            productService.setImageURL(vo);

        }

        return "redirect:list";
    }

}
