/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Abstract base class for all persistent objects. This ensures consistent
 * behavior.
 * 
 * Business key semantics used for hashCode and equals. See
 * http://docs.jboss.org
 * /hibernate/orm/3.3/reference/en-US/html/persistent-classes
 * .html#persistent-classes-equalshashcode
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@MappedSuperclass
public abstract class PersistentObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer version;
    private String uuid;
    private Date creationDate;
    private String self;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(unique = true, length = 40, nullable = false, updatable = false)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(name = "creation_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        // security - don't return reference to mutable object
        return creationDate == null ? null : new Date(creationDate.getTime());
    }

    public void setCreationDate(Date creationDate) {
        // security - don't keep reference to mutable object
        this.creationDate = (creationDate == null ? null : new Date(creationDate.getTime()));
    }

    @Transient
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @PrePersist
    public void prepersist() {
        if (getCreationDate() == null) {
            setCreationDate(new Date());
        }

        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(uuid).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PersistentObject)) {
            return false;
        }

        PersistentObject po = (PersistentObject) o;

        EqualsBuilder eq = new EqualsBuilder();
        eq.append(uuid, po.getUuid());

        return eq.isEquals();
    }

    @Override
    public String toString() {
        // ToStringBuilder tsb = new ToStringBuilder(this,
        // ToStringStyle.SHORT_PREFIX_STYLE);
        // tsb.append(id).append(uuid).append(name);
        // return tsb.toString();

        return String.format("[%s: %s, %s]", this.getClass().getSimpleName(), getId(), getUuid());
    }
}
