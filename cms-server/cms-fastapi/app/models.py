"""ORM 模型：直接映射经典版 cms.sql 中的三张表（表名/列名保持不变）。"""
from sqlalchemy import Boolean, Column, DateTime, Integer, String, Text

from .database import Base


class Account(Base):
    __tablename__ = "tb_account"

    id = Column(Integer, primary_key=True, autoincrement=True)
    username = Column("username", String(500))
    password = Column("password", String(500))  # md5 32 位小写
    creattime = Column("creatTime", DateTime)
    updattime = Column("updatTime", DateTime)


class Type(Base):
    __tablename__ = "tb_type"

    id = Column(Integer, primary_key=True, autoincrement=True)
    title = Column("title", String(8))
    weight = Column("weight", String(500))  # 历史原因为 varchar，排序时 CAST
    creattime = Column("creatTime", DateTime)
    updattime = Column("updatTime", DateTime)


class Article(Base):
    __tablename__ = "tb_article"

    id = Column(Integer, primary_key=True, autoincrement=True)
    pid = Column("pid", Integer)
    imgurl = Column("imgUrl", String(500))
    title = Column("title", String(512))
    tourl = Column("toUrl", String(500))
    weight = Column("weight", String(500))  # 历史原因为 varchar，排序时 CAST
    isbanner = Column("isBanner", Boolean)
    content = Column("content", Text)
    creattime = Column("creatTime", DateTime)
    updattime = Column("updatTime", DateTime)
