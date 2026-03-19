package com.liubingqi.auth.service.impl;

import com.liubingqi.auth.domain.po.Company;
import com.liubingqi.auth.mapper.CompanyMapper;
import com.liubingqi.auth.service.ICompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公司信息表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-19
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements ICompanyService {

}
