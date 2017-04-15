package ua.softgroup.matrix.server.persistent.entity

import javax.persistence._

import org.springframework.data.domain.Persistable
import org.springframework.util.ClassUtils

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@MappedSuperclass
@SerialVersionUID(7593520599502630581L)
abstract class AbstractEntity[PK <: java.io.Serializable] extends Persistable[PK] {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: PK = _

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Persistable#getId()
   */
  override def getId: PK = id

  /**
    * Sets the id of the entity.
    *
    * @param id the id to set
    */
  protected def setId(id: PK): Unit = {
    this.id = id
  }

  /**
    * Must be ''Transient'' in order to ensure that no JPA provider complains because of a missing setter.
    *
    * @see [[org.springframework.data.domain.Persistable#isNew()]]
    */
  @Transient
  override def isNew: Boolean = null == getId

  override def equals(obj: Any): Boolean = {
    if (null == obj) return false

    if (this == obj) return true

    if (!(getClass == ClassUtils.getUserClass(obj))) return false

    val that = obj.asInstanceOf[AbstractEntity[_ <: Serializable]]

    if (null == this.getId) false
    else this.getId == that.getId
  }

  override def hashCode: Int = {
    val prime = 17
    val hash = if (null == getId) 0 else getId.hashCode * 31
    hash + prime
  }

  override def toString = s"${this.getClass.getName}(id=$id)"

}
