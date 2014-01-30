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
 * specific language governing pestRunissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.project.student.webservice.client.impl;

import java.util.Arrays;
import java.util.List;

import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.TestRunFinderRestClient;

/**
 * Implementation of TestRunFinderRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TestRunFinderRestClientImpl extends AbstractFinderRestClientImpl<TestRun> implements
        TestRunFinderRestClient {
    private static final TestRun[] EMPTY_TEST_RUN_ARRAY = new TestRun[0];

    /**
     * Constructor.
     * 
     * @param testRunResource
     */
    public TestRunFinderRestClientImpl(final String resource) {
        super(resource, TestRun.class, TestRun[].class);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public List<TestRun> findAllTestRuns() {
        TestRun[] runs = super.getAllObjects(EMPTY_TEST_RUN_ARRAY);
        return Arrays.asList(runs);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public TestRun findTestRunByUuid(final String uuid) {
        return super.getObject(uuid);
    }
}
