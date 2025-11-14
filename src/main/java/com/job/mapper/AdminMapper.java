package com.job.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import com.job.entity.User;
import java.util.List;

@Mapper
public interface AdminMapper {
    
    @Select("SELECT COUNT(*) FROM users")
    Long countUsers();
    
    @Select("SELECT COUNT(*) FROM company_profiles")
    Long countCompanies();
    
    @Select("SELECT COUNT(*) FROM jobs")
    Long countJobs();
    
    @Select("SELECT COUNT(*) FROM resumes")
    Long countResumes();
    
    @Select("<script>" +
            "SELECT * FROM users WHERE 1=1" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone LIKE CONCAT('%', #{phone}, '%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY created_at asc LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<User> selectUserList(@Param("username") String username, 
                            @Param("phone") String phone,
                            @Param("status") Integer status,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE 1=1" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone LIKE CONCAT('%', #{phone}, '%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "</script>")
    Long countUserList(@Param("username") String username,
                      @Param("phone") String phone,
                      @Param("status") Integer status);
    
    @Update("UPDATE users SET status = #{status} WHERE id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    @Delete("DELETE FROM users WHERE id = #{userId}")
    int deleteUser(@Param("userId") Long userId);
    
    @Update("UPDATE users SET password = #{newPassword} WHERE id = #{userId}")
    int updateUserPassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
    
    // 统计某天注册用户数
    int countUserByDate(@Param("date") String date);
    // 按行业统计企业数量
    java.util.List<java.util.Map<String, Object>> countCompanyByIndustry();
}
