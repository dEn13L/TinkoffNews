package com.tinkoff.news.db

import io.requery.Entity
import io.requery.Key
import io.requery.Persistable
import io.requery.Table

@Table(name = "news")
@Entity
interface IDbNews : Persistable {

  @get:Key
  val newsId: Long
  val name: String
  val text: String
  val publicationDate: Long
  val bankInfoTypeId: Int
}

@Table(name = "news_detail")
@Entity
interface IDbNewsDetail : Persistable {

  @get:Key
  val newsId: Long
  val creationDate: Long
  val lastModificationDate: Long
  val content: String
  val bankInfoTypeId: Int
  val typeId: String
}