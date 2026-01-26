-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: job_platform
-- ------------------------------------------------------
-- Server version	5.5.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applications` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `job_id` bigint(20) NOT NULL,
                                `student_id` bigint(20) NOT NULL,
                                `resume_id` bigint(20) NOT NULL,
                                `status` varchar(20) NOT NULL DEFAULT '待处理',
                                `apply_time` datetime DEFAULT NULL,
                                `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `job_id` (`job_id`),
                                KEY `student_id` (`student_id`),
                                KEY `resume_id` (`resume_id`),
                                CONSTRAINT `applications_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`),
                                CONSTRAINT `applications_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student_profiles` (`id`),
                                CONSTRAINT `applications_ibfk_3` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (1,1,1,1,'已撤回','2025-06-05 18:01:30','2025-06-07 08:23:03'),(2,2,2,2,'已查看','2025-05-31 18:01:30','2025-05-31 10:01:30'),(3,3,3,3,'待面试','2025-05-31 18:01:30','2025-05-31 10:01:30'),(4,4,4,4,'已通过','2025-05-31 18:01:30','2025-05-31 10:01:30'),(5,5,5,5,'已拒绝','2025-05-31 18:01:30','2025-05-31 10:01:30'),(6,6,6,6,'待处理','2025-05-31 18:01:30','2025-05-31 10:01:30'),(7,7,7,7,'已查看','2025-05-31 18:01:30','2025-05-31 10:01:30'),(8,8,8,8,'待面试','2025-05-31 18:01:30','2025-05-31 10:01:30'),(9,9,9,9,'已通过','2025-05-31 18:01:30','2025-05-31 10:01:30'),(10,10,10,10,'已拒绝','2025-05-31 18:01:30','2025-05-31 10:01:30'),(11,1,1,1,'待处理','2025-06-02 16:46:14','2025-06-02 08:46:14'),(12,2,2,2,'已查看','2025-06-02 16:46:14','2025-06-02 08:46:14'),(13,3,3,3,'待面试','2025-06-02 16:46:14','2025-06-02 08:46:14'),(14,4,4,4,'已通过','2025-06-02 16:46:14','2025-06-02 08:46:14'),(15,5,5,5,'已拒绝','2025-06-02 16:46:14','2025-06-02 08:46:14'),(16,6,6,6,'待面试','2025-06-02 16:46:14','2025-06-02 15:25:48'),(17,7,7,7,'已查看','2025-06-02 16:46:14','2025-06-02 08:46:14'),(18,8,8,8,'待面试','2025-06-02 16:46:14','2025-06-02 08:46:14'),(19,9,9,9,'已通过','2025-06-02 16:46:14','2025-06-02 08:46:14'),(20,10,10,10,'已拒绝','2025-06-02 16:46:14','2025-06-02 08:46:14'),(21,30,1,1,'待处理','2025-06-02 20:03:54','2025-06-02 12:03:56'),(22,12,1,1,'待处理','2025-06-02 20:05:24','2025-06-02 12:05:24'),(23,30,1,1,'待处理','2025-06-02 22:30:33','2025-06-02 14:30:33'),(24,6,1,1,'待面试','2025-06-02 23:26:26','2025-06-02 15:30:59'),(25,1,1,1,'已通过','2025-06-02 23:26:46','2025-06-05 15:06:34'),(26,11,1,1,'已撤回','2025-06-07 16:23:36','2025-11-14 13:48:00'),(28,31,1,1,'已发送offer','2025-06-07 16:30:51','2025-06-07 08:37:21'),(29,17,1,1,'已邀请面试','2025-12-17 15:28:02','2025-12-17 07:30:00');
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `session_id` bigint(20) NOT NULL COMMENT '关联 chat_session.id',
                                `role` enum('user','assistant') NOT NULL COMMENT '发言角色',
                                `content` text NOT NULL COMMENT '消息内容',
                                `tool_invoked` varchar(100) DEFAULT NULL COMMENT '触发的工具名，如 search_jobs',
                                `tool_args` text COMMENT '工具参数（JSON 格式字符串）',
                                `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `idx_session_id` (`session_id`),
                                KEY `idx_created_at` (`created_at`),
                                CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='智能客服消息记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,6,'user','java岗位有什么',NULL,NULL,'2026-01-26 12:25:36');
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session`
--

DROP TABLE IF EXISTS `chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `user_id` bigint(20) DEFAULT NULL COMMENT '关联 user.id，若为 NULL 表示匿名访客',
                                `session_key` varchar(64) NOT NULL COMMENT '匿名会话唯一标识（UUID）',
                                `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_session_key` (`session_key`),
                                KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='智能客服会话';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
INSERT INTO `chat_session` VALUES (1,1,'1','2025-11-16 01:42:52'),(2,2,'2','2025-11-15 08:05:34'),(6,6,'6','2026-01-26 12:25:36');
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_profiles`
--

DROP TABLE IF EXISTS `company_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_profiles` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `user_id` bigint(20) NOT NULL,
                                    `company_name` varchar(100) NOT NULL,
                                    `industry` varchar(50) DEFAULT NULL,
                                    `company_size` varchar(50) DEFAULT NULL,
                                    `location` varchar(100) DEFAULT NULL,
                                    `description` text,
                                    `logo_url` varchar(255) DEFAULT NULL,
                                    `website` varchar(255) DEFAULT NULL,
                                    `verification_status` tinyint(4) DEFAULT '0',
                                    `audit_status` enum('pending','approved','rejected') DEFAULT 'pending',
                                    `audit_reason` varchar(255) DEFAULT NULL,
                                    `audit_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `user_id` (`user_id`),
                                    CONSTRAINT `company_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_profiles`
--

LOCK TABLES `company_profiles` WRITE;
/*!40000 ALTER TABLE `company_profiles` DISABLE KEYS */;
INSERT INTO `company_profiles` VALUES (1,6,'字节跳动','互联网','10000+','北京','知名互联网公司','1','https://www.bytedance.com',1,'pending',NULL,NULL),(2,7,'阿里巴巴','电商','10000+','杭州','中国最大电商公司','','https://www.alibaba.com',1,'pending',NULL,NULL),(3,8,'腾讯','互联网','10000+','深圳','中国领先的互联网公司','','https://www.tencent.com',1,'pending',NULL,NULL),(4,11,'百度','搜索','10000+','北京','中国搜索引擎巨头','','https://www.baidu.com',1,'pending',NULL,NULL),(5,21,'华为','通信','10000+','深圳','全球领先的ICT解决方案供应商','','https://www.huawei.com',1,'pending',NULL,NULL),(6,22,'京东','电商','10000+','北京','中国大型自营式电商','','https://www.jd.com',1,'pending',NULL,NULL),(7,23,'美团','生活服务','10000+','北京','中国领先的生活服务电子商务平台','','https://www.meituan.com',1,'pending',NULL,NULL),(8,24,'网易','互联网','10000+','杭州','中国知名互联网公司','','https://www.163.com',1,'pending',NULL,NULL),(9,25,'小米','智能硬件','10000+','北京','全球知名智能硬件公司','','https://www.mi.com',1,'pending',NULL,NULL),(10,26,'滴滴出行','出行','10000+','北京','中国领先的移动出行平台','','https://www.didiglobal.com',1,'pending',NULL,NULL),(11,31,'字节跳动','互联网','10000+','北京','中国领先的互联网科技公司','https://logo.bytedance.com/1.png','https://www.bytedance.com',1,'pending',NULL,NULL),(12,32,'阿里巴巴','电商','10000+','杭州','全球知名的电商与云计算公司','https://logo.alibaba.com/2.png','https://www.alibaba.com',1,'pending',NULL,NULL),(13,33,'腾讯','互联网','10000+','深圳','中国最大的互联网综合服务提供商','https://logo.tencent.com/3.png','https://www.tencent.com',1,'pending',NULL,NULL),(14,34,'百度','互联网','10000+','北京','中国领先的AI公司','https://logo.baidu.com/4.png','https://www.baidu.com',1,'pending',NULL,NULL),(15,35,'美团','生活服务','10000+','北京','中国领先的生活服务电子商务平台','https://logo.meituan.com/5.png','https://www.meituan.com',1,'pending',NULL,NULL),(16,36,'京东','电商','10000+','北京','中国大型综合网络零售商','https://logo.jd.com/6.png','https://www.jd.com',1,'pending',NULL,NULL),(17,37,'滴滴出行','出行','10000+','北京','中国领先的移动出行平台','https://logo.didichuxing.com/7.png','https://www.didiglobal.com',1,'pending',NULL,NULL),(18,38,'小米','智能硬件','10000+','北京','全球知名智能硬件公司','https://logo.mi.com/8.png','https://www.mi.com',1,'pending',NULL,NULL),(19,39,'快手','短视频','10000+','北京','中国领先的短视频平台','https://logo.kuaishou.com/9.png','https://www.kuaishou.com',1,'pending',NULL,NULL),(20,40,'网易','互联网','10000+','杭州','中国知名互联网公司','https://logo.netease.com/10.png','https://www.163.com',1,'pending',NULL,NULL),(21,41,'携程','旅游','10000+','上海','中国领先的在线旅行服务公司','https://logo.ctrip.com/11.png','https://www.ctrip.com',1,'pending',NULL,NULL),(22,42,'B站','视频','5000-10000','上海','中国领先的视频弹幕网站','https://logo.bilibili.com/12.png','https://www.bilibili.com',1,'pending',NULL,NULL),(23,43,'知乎','知识问答','1000-5000','北京','中国领先的知识问答社区','https://logo.zhihu.com/13.png','https://www.zhihu.com',1,'pending',NULL,NULL),(24,44,'猎聘','招聘','1000-5000','北京','知名招聘平台','https://logo.liepin.com/14.png','https://www.liepin.com',1,'pending',NULL,NULL),(25,45,'Boss直聘','招聘','1000-5000','北京','知名招聘平台','https://logo.zhipin.com/15.png','https://www.zhipin.com',1,'pending',NULL,NULL),(26,46,'同程艺龙','旅游','1000-5000','苏州','知名在线旅游平台','https://logo.ly.com/16.png','https://www.ly.com',1,'pending',NULL,NULL),(27,47,'去哪儿','旅游','1000-5000','北京','知名在线旅游平台','https://logo.qunar.com/17.png','https://www.qunar.com',1,'pending',NULL,NULL),(28,48,'携程旅行','旅游','1000-5000','上海','知名在线旅游平台','https://logo.ctrip.com/18.png','https://www.ctrip.com',1,'pending',NULL,NULL),(29,49,'贝壳找房','房产','10000+','北京','中国领先的房产服务平台','https://logo.ke.com/19.png','https://www.ke.com',1,'pending',NULL,NULL),(30,50,'安居客','房产','1000-5000','上海','知名房产信息平台','https://logo.anjuke.com/20.png','https://www.anjuke.com',0,'pending',NULL,NULL),(31,56,'未命名企业56',NULL,NULL,NULL,NULL,NULL,NULL,0,'pending',NULL,NULL),(32,57,'未命名企业57',NULL,NULL,NULL,NULL,NULL,NULL,1,'pending',NULL,NULL);
/*!40000 ALTER TABLE `company_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `company_id` bigint(20) NOT NULL,
                        `title` varchar(100) NOT NULL,
                        `description` text,
                        `requirements` text,
                        `salary_range` varchar(50) DEFAULT NULL,
                        `location` varchar(100) DEFAULT NULL,
                        `job_type` enum('全职','兼职','实习') DEFAULT NULL,
                        `education_requirement` enum('不限','专科','本科','硕士','博士') DEFAULT NULL,
                        `experience_requirement` varchar(50) DEFAULT NULL,
                        `status` tinyint(4) DEFAULT '1',
                        `created_at` datetime DEFAULT NULL,
                        `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`),
                        KEY `company_id` (`company_id`),
                        CONSTRAINT `jobs_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
INSERT INTO `jobs` VALUES (1,1,'Java开发工程师','负责后端开发','精通Java','15k-30k','北京','全职','本科','1年',0,'2025-05-31 18:01:00','2025-12-14 09:46:54'),(2,2,'前端开发工程师','负责前端开发','精通Vue','12k-25k','杭州','全职','本科','1年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(3,3,'产品经理','负责产品设计','有产品经验','20k-40k','深圳','全职','本科','2年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(4,4,'算法工程师','负责算法研发','精通算法','25k-50k','北京','全职','硕士','2年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(5,5,'测试工程师','负责测试','有测试经验','10k-20k','深圳','全职','本科','1年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(6,6,'运维工程师','负责运维1','有运维经验','13k-22k','北京','全职','本科','1年',1,'2025-05-31 18:01:00','2025-06-02 14:33:44'),(7,7,'数据分析师','负责数据分析','精通数据分析','18k-35k','杭州','全职','硕士','2年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(8,8,'UI设计师','负责UI设计','有设计经验','12k-20k','北京','全职','本科','1年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(9,9,'市场专员','负责市场推广','有市场经验','8k-15k','北京','全职','本科','1年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(10,10,'人力资源','负责HR相关工作','有HR经验','10k-18k','深圳','全职','本科','1年',1,'2025-05-31 18:01:00','2025-05-31 10:01:00'),(11,11,'Java开发工程师','负责后端开发','精通Java，熟悉Spring','15k-30k','北京','全职','本科','3年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(12,12,'前端开发工程师','负责Web前端开发','熟悉Vue/React','12k-25k','上海','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(13,13,'产品经理','负责产品设计与需求分析','有互联网产品经验','18k-35k','深圳','全职','本科','3年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(14,14,'UI设计师','负责界面设计','有美术基础','10k-20k','广州','全职','本科','1年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(15,15,'测试工程师','负责软件测试','熟悉自动化测试','10k-18k','北京','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(16,16,'运维工程师','负责系统运维','熟悉Linux','13k-22k','上海','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(17,17,'数据分析师','负责数据分析','熟悉SQL和Python','15k-28k','北京','全职','硕士','1年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(18,18,'市场专员','负责市场推广','有市场推广经验','8k-15k','深圳','全职','本科','1年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(19,19,'人力资源专员','负责招聘与员工管理','有人力资源经验','9k-16k','北京','全职','本科','1年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(20,20,'销售经理','负责销售团队管理','有销售管理经验','20k-40k','上海','全职','本科','3年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(21,21,'C++开发工程师','负责C++相关开发','精通C++','16k-32k','北京','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(22,22,'Python开发工程师','负责Python开发','精通Python','15k-30k','上海','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(23,23,'算法工程师','负责算法设计与实现','有算法竞赛经验','20k-40k','深圳','全职','硕士','1年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(24,24,'数据挖掘工程师','负责数据挖掘','熟悉数据挖掘算法','18k-36k','北京','全职','硕士','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(25,25,'Android开发工程师','负责Android开发','熟悉Android SDK','14k-28k','广州','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(26,26,'iOS开发工程师','负责iOS开发','熟悉iOS SDK','14k-28k','上海','全职','本科','2年及以上',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(27,27,'运维实习生','协助运维工作','计算机相关专业','3k-5k','北京','实习','本科','不限',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(28,28,'前端实习生','协助前端开发','计算机相关专业','3k-5k','上海','实习','本科','不限',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(29,29,'产品实习生','协助产品经理','有较强沟通能力','3k-5k','深圳','实习','本科','不限',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(30,30,'测试实习生','协助测试工作','细心负责','3k-5k','广州','实习','本科','不限',1,'2025-06-02 11:33:08','2025-06-02 03:33:08'),(31,1,'前端','会vue',NULL,'10k+','西安',NULL,NULL,NULL,1,NULL,'2025-06-05 14:25:59'),(32,1,'go','',NULL,'10k-12k','北京',NULL,NULL,NULL,1,NULL,'2025-12-14 09:49:02');
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `receiver_id` bigint(20) NOT NULL,
                                 `sender_id` bigint(20) DEFAULT NULL,
                                 `application_id` bigint(20) DEFAULT NULL,
                                 `type` char(20) NOT NULL DEFAULT '反馈',
                                 `content` text NOT NULL,
                                 `is_read` tinyint(4) DEFAULT '0',
                                 `created_at` datetime DEFAULT NULL,
                                 `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 KEY `receiver_id` (`receiver_id`),
                                 KEY `sender_id` (`sender_id`),
                                 KEY `application_id` (`application_id`),
                                 CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
                                 CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
                                 CONSTRAINT `notifications_ibfk_3` FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (22,6,1,28,'简历投递','张三向贵公司【字节跳动】的岗位【前端】投递了简历。',1,'2025-06-07 16:30:51','2025-06-07 08:31:25'),(23,1,6,28,'面试邀请','【字节跳动】的职位【前端】邀请你进行面试，请尽快回复。',1,'2025-06-07 16:31:34','2025-06-07 08:31:59'),(24,1,6,28,'反馈','【字节跳动】的职位【前端】已通过你的简历，请等待后续通知。',1,'2025-06-07 16:37:21','2025-06-07 08:37:21'),(25,6,1,28,'学生已读','学生张三已读了你给他发送的反馈消息。',1,'2025-06-07 16:39:05','2025-06-07 08:39:05'),(26,1,6,28,'offer','【字节跳动】的职位【前端】向你发送了offer！',1,'2025-11-14 21:46:19','2025-11-14 13:46:19'),(27,6,1,28,'学生已读','学生张三已读了你给他发送的offer消息。',1,'2025-11-14 21:48:08','2025-11-14 13:48:08'),(28,37,1,29,'简历投递','张三向贵公司【滴滴出行】的岗位【数据分析师】投递了简历。',0,'2025-12-17 15:28:02','2025-12-17 07:28:02'),(29,1,37,29,'面试邀请','【滴滴出行】的职位【数据分析师】邀请你进行面试，请尽快回复。',1,'2025-12-17 15:30:00','2025-12-17 07:30:00'),(30,37,1,29,'学生已读','学生张三已读了你给他发送的面试邀请消息。',0,'2025-12-17 15:30:10','2025-12-17 07:30:10');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resumes`
--

DROP TABLE IF EXISTS `resumes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resumes` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `student_id` bigint(20) NOT NULL,
                           `title` varchar(100) DEFAULT NULL,
                           `content` text,
                           `template_id` int(11) DEFAULT NULL,
                           `status` tinyint(4) DEFAULT '1',
                           `created_at` datetime DEFAULT NULL,
                           `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `student_id` (`student_id`),
                           CONSTRAINT `resumes_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resumes`
--

LOCK TABLES `resumes` WRITE;
/*!40000 ALTER TABLE `resumes` DISABLE KEYS */;
INSERT INTO `resumes` VALUES (1,1,'张三-后端简历','精通Java、Spring Boot',1,1,'2025-06-03 18:01:13','2025-06-07 08:12:58'),(2,2,'李四-前端简历','精通Vue、React',1,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(3,3,'王五-算法简历','AI算法、机器学习',2,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(4,4,'赵六-安全简历','网络安全、渗透测试',2,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(5,5,'钱七-硬件简历','嵌入式开发',1,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(6,6,'孙八-物联网简历','IoT开发',1,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(7,7,'周九-通信简历','5G通信',2,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(8,8,'吴十-大数据简历','Hadoop、Spark',2,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(9,9,'郑十一-网络简历','网络运维',1,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(10,10,'冯十二-自动化简历','自动化控制',1,1,'2025-05-31 18:01:13','2025-05-31 10:01:13'),(11,1,'java开发','111111',NULL,1,NULL,'2025-12-17 07:21:09');
/*!40000 ALTER TABLE `resumes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_profiles`
--

DROP TABLE IF EXISTS `student_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_profiles` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `user_id` bigint(20) NOT NULL,
                                    `real_name` varchar(50) DEFAULT NULL,
                                    `gender` enum('male','female','other') DEFAULT NULL,
                                    `birth_date` date DEFAULT NULL,
                                    `university` varchar(100) DEFAULT NULL,
                                    `major` varchar(100) DEFAULT NULL,
                                    `education` enum('专科','本科','硕士','博士') DEFAULT NULL,
                                    `graduation_year` year(4) DEFAULT NULL,
                                    `skills` text,
                                    `introduction` text,
                                    PRIMARY KEY (`id`),
                                    KEY `user_id` (`user_id`),
                                    CONSTRAINT `student_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_profiles`
--

LOCK TABLES `student_profiles` WRITE;
/*!40000 ALTER TABLE `student_profiles` DISABLE KEYS */;
INSERT INTO `student_profiles` VALUES (1,1,'张三','male','2000-01-01','清华大学','计算机','本科',2022,'Java,Python','热爱编程'),(2,2,'李四','female','2001-02-02','北京大学','软件工程','本科',2023,'C++,JavaScript','喜欢算法'),(3,3,'王五','male','2002-03-03','复旦大学','人工智能','硕士',2024,'AI,机器学习','AI达人'),(4,4,'赵六','female','2000-04-04','上海交通大学','信息安全','本科',2022,'安全,渗透','安全专家'),(5,5,'钱七','male','2001-05-05','浙江大学','电子信息','本科',2023,'嵌入式,硬件','硬件极客'),(6,13,'孙八','male','2000-06-06','南京大学','物联网','本科',2022,'IoT,传感器','物联网爱好者'),(7,14,'周九','female','2001-07-07','武汉大学','通信工程','本科',2023,'通信,5G','通信达人'),(8,15,'吴十','male','2002-08-08','中山大学','大数据','硕士',2024,'大数据,Hadoop','数据分析师'),(9,16,'郑十一','female','2000-09-09','厦门大学','网络工程','本科',2022,'网络,运维','网络专家'),(10,17,'冯十二','male','2001-10-10','山东大学','自动化','本科',2023,'自动化,控制','自动化工程师'),(11,18,'张小明','male','2000-05-15','北京大学','计算机科学与技术','本科',2023,'Java, Python, MySQL','热爱编程，擅长算法设计，有实习经验。'),(12,19,'李华','male','1999-11-22','清华大学','软件工程','硕士',2024,'C++, Linux, 深度学习','研究方向为人工智能，发表过2篇论文。'),(13,20,'王芳','female','2001-03-08','复旦大学','数据科学','本科',2023,'Python, R, 数据分析','对大数据处理有浓厚兴趣。');
/*!40000 ALTER TABLE `student_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `username` varchar(50) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `phone` varchar(20) DEFAULT NULL,
                         `role` enum('student','company','admin') NOT NULL,
                         `status` tinyint(4) DEFAULT '1',
                         `created_at` datetime DEFAULT NULL,
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'234234','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student1@example.com','1111111111','student',1,'2025-05-31 18:00:06','2025-06-07 08:22:45',NULL),(2,'student2','$2a$10$VOHEH.WMquSmdFk4bxoTe.D5Z9bww8.faLzaHGS2D0vOLuL05tXh6','student2@example.com','1111111112','student',1,'2025-05-31 18:00:06','2025-06-05 17:04:35',NULL),(3,'student3','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student3@example.com','1111111113','student',1,'2025-05-31 18:00:06','2025-05-31 15:13:26',NULL),(4,'student4','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student4@example.com','1111111114','student',1,'2025-05-31 18:00:06','2025-05-31 15:13:26',NULL),(5,'student5','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student5@example.com','1111111115','student',1,'2025-05-31 18:00:06','2025-05-31 15:13:26',NULL),(6,'123123','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company1@example.com','2222222221','company',1,'2025-05-31 18:00:06','2025-06-02 06:19:56',NULL),(7,'company2','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company2@example.com','2222222222','company',1,'2025-05-31 18:00:06','2025-05-31 15:13:26',NULL),(8,'company3','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company3@example.com','2222222223','company',1,'2025-05-31 18:00:06','2025-05-31 15:13:26',NULL),(9,'admin','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin1@example.com','3333333331','admin',1,'2025-05-31 18:00:06','2025-05-31 15:13:34',NULL),(10,'admin2','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin2@example.com','3333333332','admin',1,'2025-05-31 18:00:06','2025-05-31 15:13:13',NULL),(11,'aaa','$2a$10$AuuSORVt6qPlsqAbB3Nf0eBagc1jz.4gy/yrEmfmzzdwUPjk29tuu','3118657467@qq.com',NULL,'company',0,'2025-06-06 00:46:51','2025-06-05 16:56:50',NULL),(13,'student1','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student1@example.com','1111111111','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(14,'student2','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student2@example.com','1111111112','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(15,'student3','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student3@example.com','1111111113','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(16,'student4','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student4@example.com','1111111114','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(17,'student5','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student5@example.com','1111111115','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(18,'student6','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student6@example.com','1111111116','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(19,'student7','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student7@example.com','1111111117','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(20,'student8','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','student8@example.com','1111111118','student',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(21,'company1','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company1@example.com','2222222221','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(22,'company2','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company2@example.com','2222222222','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(23,'company3','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company3@example.com','2222222223','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(24,'company4','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company4@example.com','2222222224','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(25,'company5','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company5@example.com','2222222225','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(26,'company6','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','company6@example.com','2222222226','company',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(27,'admin1','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin1@example.com','3333333331','admin',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(28,'admin2','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin2@example.com','3333333332','admin',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(29,'admin3','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin3@example.com','3333333333','admin',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(30,'admin4','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','admin4@example.com','3333333334','admin',1,'2025-06-02 20:59:18','2025-06-02 13:08:39',NULL),(31,'tech_solutions','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','contact@techsolutions.com','13800138001','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(32,'green_energy','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','info@greenenergy.com','13800138002','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(33,'global_finance','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','support@globalfinance.com','13800138003','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(34,'creative_design','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','hello@creativedesign.com','13800138004','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(35,'health_care','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','service@healthcare.com','13800138005','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(36,'logistics_pro','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','contact@logisticspro.com','13800138006','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(37,'edu_connect','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','info@educonnect.com','13800138007','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(38,'food_express','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','support@foodexpress.com','13800138008','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(39,'smart_home','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','sales@smarthome.com','13800138009','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(40,'travel_agency','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','book@travelagency.com','13800138010','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(41,'ai_innovations','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','contact@aiinnovations.com','13800138011','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(42,'eco_builders','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','info@ecobuilders.com','13800138012','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(43,'digital_marketing','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','team@digitalmarketing.com','13800138013','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(44,'auto_masters','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','service@automasters.com','13800138014','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(45,'fashion_hub','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','support@fashionhub.com','13800138015','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(46,'cloud_services','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','help@cloudservices.com','13800138016','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(47,'security_systems','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','sales@securitysystems.com','13800138017','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(48,'media_production','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','contact@mediaproduction.com','13800138018','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(49,'sports_gear','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','info@sportsgear.com','13800138019','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(50,'consulting_group','$2a$10$bhsLqk03s8hgHVmbf6CUHOyop7zgLM4kd1CLk4Cas/B4Bowoyulh.','enquiry@consultinggroup.com','13800138020','company',1,'2025-06-02 21:07:04','2025-06-02 13:08:39',NULL),(56,'111','$2a$10$K3/7aHuoJ4eSX3uaXKy0/emTyyZLcR2IWTH0P8OS.1BSeWyoxanyi','3118657467@qq.com','15389653567','company',1,'2025-06-07 15:31:14','2025-06-07 07:31:14',NULL),(57,'111111','$2a$10$4Y5CtdOXg6VjEHQPgVftderkuqwp9kJXwjPO99cp8ZWm7tr1TUxau','fajdjfk@gmail.com','14278656789','company',1,'2025-12-14 16:19:24','2025-12-14 08:19:24',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-26 20:56:29
