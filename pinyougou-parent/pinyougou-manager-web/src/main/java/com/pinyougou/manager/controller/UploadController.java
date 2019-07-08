package com.pinyougou.manager.controller;

import com.pinyougou.utils.dfs.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dfs")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {

        //获取全路径名称,截取,获取后缀名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        try {
            //使用工具创建fastDfs的客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");

            //上传(文件的字节类型数组,后缀名称)
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接返回的 url 和 ip 地址，拼装成完整的 url
            String s = FILE_SERVER_URL + path;
            return new Result(true, s);
        }catch (Exception e){
            return new Result(false,"上传失败!");
        }
    }
}
