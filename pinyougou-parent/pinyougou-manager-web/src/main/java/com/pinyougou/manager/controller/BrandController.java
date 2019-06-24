package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findAllPage")
    public PageInfo<TbBrand> findAllPage(Integer page,Integer size){

        PageInfo<TbBrand> pageInfo = brandService.findAllPage(page, size);

        return pageInfo;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        Result result = new Result();

        try {
            brandService.add(tbBrand);
            result.setSuccess(true);
            result.setMessage("添加成功!");
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage("添加失败,请联系管理员!或请稍后重试");
        }

        return result;
    }

    @RequestMapping("/findById")
    public TbBrand findByid(Long id){
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        Result result = new Result();
        try {
        brandService.update(tbBrand);
            result.setSuccess(true);
            result.setMessage("修改成功!");
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage("修改失败,请联系管理员!或请稍后重试");
        }
        return result;
    }
}
