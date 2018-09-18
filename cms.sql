/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.5.28 : Database - weichatarticle
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`weichatarticle` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `weichatarticle`;

/*Table structure for table `tb_account` */

DROP TABLE IF EXISTS `tb_account`;

CREATE TABLE `tb_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(500) DEFAULT NULL,
  `password` varchar(500) DEFAULT NULL,
  `updatTime` datetime DEFAULT NULL,
  `creatTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `tb_account` */

insert  into `tb_account`(`id`,`username`,`password`,`updatTime`,`creatTime`) values (1,'13695316318','e10adc3949ba59abbe56e057f20f883e','2018-09-11 11:18:10','2018-09-11 11:18:18');

/*Table structure for table `tb_article` */

DROP TABLE IF EXISTS `tb_article`;

CREATE TABLE `tb_article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL,
  `imgUrl` varchar(500) DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `content` text,
  `toUrl` varchar(500) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `isBanner` tinyint(1) DEFAULT NULL,
  `updatTime` datetime DEFAULT NULL,
  `creatTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `tb_article` */

insert  into `tb_article`(`id`,`pid`,`imgUrl`,`title`,`content`,`toUrl`,`weight`,`isBanner`,`updatTime`,`creatTime`) values (2,1,'/img/fe32f07a-a4cf-4ebf-801e-8a577e2e2dc7.png','cms小程序上线啦','微信小程序版的cms系统 上线啦',NULL,NULL,0,'2018-09-17 18:51:59',NULL),(3,1,'/img/cec0e167-2ce4-4f9f-b557-64d7e5e54cbf.png','haha','2额太过分的不规范',NULL,NULL,1,'2018-09-17 17:24:12','2018-09-17 17:24:12'),(4,NULL,'/img/3e3c4175-d11b-4932-a209-32fa1603ad67.png','网站教学','11111111',NULL,NULL,1,'2018-09-17 17:25:16','2018-09-17 17:25:16'),(5,2,'/img/0a6dad74-04ee-4f5f-9585-8d52ebabadbd.png','长城','天高云淡，望断南飞雁，不到长城非好汉',NULL,NULL,0,'2018-09-17 18:53:44','2018-09-17 18:53:44'),(6,NULL,'/img/8fea6f79-953c-49cb-be1d-71088137dc2c.png','bannerimg1','1111111111111',NULL,NULL,1,'2018-09-17 19:02:18','2018-09-17 19:02:18'),(7,3,'/img/07ffcb41-4ef2-463d-b2e4-30783d51f6df.jpg','网络课堂开课了','开课啦开课啦开课啦',NULL,NULL,0,'2018-09-17 19:03:36','2018-09-17 19:03:36');

/*Table structure for table `tb_type` */

DROP TABLE IF EXISTS `tb_type`;

CREATE TABLE `tb_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(8) DEFAULT NULL,
  `weight` varchar(500) DEFAULT NULL,
  `updatTime` datetime DEFAULT NULL,
  `creatTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `tb_type` */

insert  into `tb_type`(`id`,`title`,`weight`,`updatTime`,`creatTime`) values (1,'通知公告','0',NULL,NULL),(2,'行业新闻','-3',NULL,NULL),(3,'网站教学','2','2018-09-17 19:02:40','2018-09-17 19:02:40');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
