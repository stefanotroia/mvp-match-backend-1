package it.stefanotroia.mvpmatch.utils.repository

class PageResponse<T>(
  var items: List<T> = arrayListOf(),
  var offset: Int,
  var limit: Int,
  var total: Int
)
