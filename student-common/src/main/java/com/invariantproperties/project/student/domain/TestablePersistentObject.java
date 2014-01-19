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

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Abstract base class for all persistent objects that include a Test UUID.
 * 
 * This class supports a form of data sharding for testing purposes. Normal data
 * should have a null value, test data should have a unique value that
 * references a TestRun object.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@MappedSuperclass
public abstract class TestablePersistentObject extends PersistentObject {
    private static final long serialVersionUID = 1L;
    private TestRun testRun;

    /**
     * Fetch testRun object. We use lazy fetching since we rarely care about the
     * contents of this object - we just want to ensure referential integrity to
     * an existing testRun object when persisting a TPO.
     * 
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "test_run_pkey")
    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    @Transient
    public boolean isTestData() {
        return testRun != null;
    }
}
