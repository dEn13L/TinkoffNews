package com.tinkoff.news.db

import io.requery.*

@Table(name = "news")
@Entity
interface IDbNews : Persistable {

  @get:Key
  val newsId: Long
  val name: String
  val text: String
  val publicationDate: Long
  val bankInfoTypeId: Int

  @get:ForeignKey
  @get:OneToOne
  var newsDetail: IDbNewsDetail?
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

  @get:OneToOne(mappedBy = "newsDetail")
  val news: IDbNews
}