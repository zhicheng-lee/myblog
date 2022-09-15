package com.lee.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.blog.dto.ArchiveDTO;
import com.lee.blog.entity.Article;
import com.lee.blog.vo.PageResult;

/**
 * 文章
 *
 * @author zhicheng lee
 * @date 2022-09-11 19:27:40
 */
public interface ArticleService extends IService<Article> {

    PageResult<ArchiveDTO> listArchives(Integer pageNum, Integer pageSize);
}
