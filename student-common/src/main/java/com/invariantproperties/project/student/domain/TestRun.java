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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Integration test run bean. "Live" integration tests should create a TestRun
 * object and set their testRun field appropriately. This keeps test data
 * separate from the rest of the application.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@XmlRootElement
@Entity
@Table(name = "test_run")
@AttributeOverride(name = "id", column = @Column(name = "test_run_pkey"))
public class TestRun extends PersistentObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private Date testDate;
    private String user;
    private List<TestablePersistentObject> objects = Collections.emptyList();

    @Column(length = 80, unique = false, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "test_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTestDate() {
        // security - don't return reference to mutable object
        return testDate == null ? null : new Date(testDate.getTime());
    }

    public void setTestDate(Date testDate) {
        // security - don't keep reference to mutable object
        this.testDate = (testDate == null ? null : new Date(testDate.getTime()));
    }

    @Column(name = "username", length = 40, unique = false, updatable = false)
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // @OneToMany(cascade = CascadeType.ALL)
    @Transient
    public List<TestablePersistentObject> getObjects() {
        return objects;
    }

    public void setObjects(List<TestablePersistentObject> objects) {
        this.objects = objects;
    }

    /**
     * This is similar to standard prepersist method but we also set default
     * values for everything else.
     */
    @PrePersist
    public void prepersist() {
        if (getCreationDate() == null) {
            setCreationDate(new Date());
        }

        if (getTestDate() == null) {
            setTestDate(new Date());
        }

        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }

        if (getUser() == null) {
            setUser(System.getProperty("user.name"));
        }

        if (name == null) {
            setName("test run " + getUuid());
        }
    }
}
