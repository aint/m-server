package ua.softgroup.matrix.server.service

import java.util.Optional

import org.springframework.data.jpa.domain.AbstractPersistable

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
trait GenericEntityService[T] {

  /**
    * Returns an entity by the given primary ''id''
    *
    * @param id entity's primary key
    * @return the entity with the given ''id''
    */
  def getById(id: Long): Optional[T]

  /**
    * Saves or update an entity in a data source
    *
    * @param entity entity's instance
    * @return the persisted entity
    */
  def save(entity: T): T

  /**
    * Checks entity existence by the given primary ''id''
    *
    * @param id entity's primary key
    * @return ''true'' if an entity with the given ''id'' exists; ''false'' otherwise
    */
  def isExist(id: Long): Boolean

}
