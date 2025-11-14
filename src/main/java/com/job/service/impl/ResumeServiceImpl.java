package com.job.service.impl;

import com.job.entity.Resume;
import com.job.mapper.ResumeMapper;
import com.job.service.ResumeService;
import com.job.common.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeMapper resumeMapper;

    @Override
    public PageResult<Resume> getResumeList(Long studentId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Resume> records = resumeMapper.selectByStudentIdPaged(studentId, offset, pageSize);
        int total = resumeMapper.countByStudentId(studentId);
        return new PageResult<>(records, total, page, pageSize);
    }

    @Override
    public void createResume(Resume resume) {
        resumeMapper.insert(resume);
    }

    @Override
    public void updateResume(Resume resume) {
        resumeMapper.updateById(resume);
    }

    @Override
    public void deleteResume(Long resumeId) {
        resumeMapper.deleteById(resumeId);
    }
} 