package com.lgh.test;

import com.google.ads.googleads.v16.common.ResponsiveDisplayAdInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class MainController {
    @RequestMapping("/")
    public String home() {
        return "hello";
    }

    @PostMapping("/upload")
    public String handleContentUpload(@RequestParam("image") MultipartFile image,
                                      @RequestParam("title") String title,
                                      @RequestParam("description") String description,
                                      @RequestParam("url") String url,
                                      @RequestParam("keywords") String keywords,
                                      @RequestParam("negativeKeywords") String negativeKeywords,
                                      @RequestParam("otherKeywords") String otherKeywords) {
        if (image.isEmpty() || title.isEmpty() || description.isEmpty() || url.isEmpty() || keywords.isEmpty() || negativeKeywords.isEmpty()|| otherKeywords.isEmpty() ) {
            return "Please fill in all fields";
        }

        try {
            // 处理关键词和负面关键词
            String[] keywordList = keywords.split(",");
            String[] negativeKeywordList = negativeKeywords.split(",");
            String[] otherKeywordsList = otherKeywords.split(",");
            UUID uuid = UUID.randomUUID();
            // 获取文件后缀名
            String originalFileName = image.getOriginalFilename();
            String fileExtension = null;
            if (originalFileName != null) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            // 获取当前目录的绝对路径
            String uploadDir = new File(Setting.PicLocation).getAbsolutePath();
            // 构建文件路径
            String filePath = uploadDir + "/" + uuid+fileExtension;
            // 保存文件
            image.transferTo(new File(filePath));

            String IMAGE_URL="/pic/"+uuid+fileExtension;







            // 处理其他字段，可以将它们存储到数据库或进行其他操作
            // 这里只是简单地将它们返回
            return "Content uploaded successfully:" + "<br>" +
                    "Title: " + title + "<br>" +
                    "Short Description: " + description + "<br>" +
                    "URL to Landing Page: " + url + "<br>" +
                    "Image uploaded successfully: " + originalFileName + "<br>" +
                    "Keywords: " + String.join(", ", keywordList) + "<br>" +
                    "Negative Keywords: " + String.join(", ", negativeKeywordList);
        } catch (IOException e) {
            return "Failed to upload content: " + e.getMessage();
        }
    }
}


