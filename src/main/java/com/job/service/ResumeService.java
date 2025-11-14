package com.job.service;

import com.job.entity.Resume;
import com.job.common.page.PageResult;

public interface ResumeService {
    PageResult<Resume> getResumeList(Long studentId, int page, int pageSize);
    void createResume(Resume resume);
    void updateResume(Resume resume);
    void deleteResume(Long resumeId);
} 