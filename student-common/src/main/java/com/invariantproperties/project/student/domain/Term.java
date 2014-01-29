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

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Term bean.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@XmlRootElement
@Entity
@Table(name = "term")
@AttributeOverride(name = "id", column = @Column(name = "term_pkey"))
public class Term extends TestablePersistentObject {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Section> sections;
    private List<Classroom> classrooms;

    @Column(length = 80, unique = false, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get list of course sections for this term.
     * 
     * @return
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "term")
    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    /**
     * Get list of all available classrooms for this term. N.B., this is not a
     * list of <i>unassigned</i> classrooms.
     * 
     * @return
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "term")
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }
}