package controllers

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
import javax.inject.Inject


/**
  * Created by srya0001 on 1/18/2017.
  */
class Filters @Inject() (val corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter){
}
