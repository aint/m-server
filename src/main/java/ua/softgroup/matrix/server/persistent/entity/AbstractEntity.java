package ua.softgroup.matrix.server.persistent.entity;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.util.ClassUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@MappedSuperclass
public abstract class AbstractEntity<PK extends Serializable> implements Persistable<PK> {
    private static final long serialVersionUID = 7593520599502630581L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private PK id;

    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Persistable#getId()
	 */
    @Override
    public PK getId() {
        return id;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    protected void setId(final PK id) {
        this.id = id;
    }

    /**
     * Must be {@link Transient} in order to ensure that no JPA provider complains because of a missing setter.
     *
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(ClassUtils.getUserClass(obj))) {
            return false;
        }
        AbstractPersistable<?> that = (AbstractPersistable<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }
}
