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

  @get:OneToOne(mappedBy = "news")
  var newsDetail: IDbNewsDetail?
}

@Table(name = "news_detail")
@Entity
interface IDbNewsDetail : Persistable {

  @get:Key @get:Generated
  val id: Long
  val creationDate: Long
  val lastModificationDate: Long
  val content: String
  val bankInfoTypeId: Int
  val typeId: String

  @get:ForeignKey
  @get:OneToOne(cascade = arrayOf(CascadeAction.NONE))
  var news: IDbNews
}