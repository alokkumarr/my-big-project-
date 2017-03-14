package sncr.metadata.engine.relations

import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MetadataNodeCanSearch

/**
  * Created by srya0001 on 3/11/2017.
  */
class RelationNode
  extends MetadataNodeCanSearch
  with Relation
{
  override val m_log: Logger = LoggerFactory.getLogger(classOf[RelationNode].getName)


}
