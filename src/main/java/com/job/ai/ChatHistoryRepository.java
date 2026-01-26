package com.job.ai;

import java.util.List;
public interface ChatHistoryRepository {
    /**
     * 保存聊天记录
     */
    void save(String userId);
    /**
     */
    void delete(String userId);

}
