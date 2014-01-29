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

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classroom bean.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@XmlRootElement
@Entity
@Table(name = "classroom")
@AttributeOverride(name = "id", column = @Column(name = "classroom_pkey"))
public class Classroom extends TestablePersistentObject {
    private static final long serialVersionUID = 1L;

    private String name;
    // time and day of week
    private Term term; // indicates start and end date
    private Section section;

    @Column(length = 80, unique = false, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get associated term - each term gets a new set of classrooms.
     * 
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "term", nullable = true)
    // TODO add support in unit tests so nullable=false
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    /**
     * Get section assigned to this classroom. It will be null if the classroom
     * is unassigned.
     * 
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "section", nullable = true)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}