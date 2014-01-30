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
package com.invariantproperties.project.student.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.invariantproperties.project.student.domain.TestRun;

public class DummyTestRunService implements TestRunService {
    private static final Logger log = Logger.getLogger(DummyTestRunService.class);
    private Map<String, TestRun> cache = Collections.synchronizedMap(new HashMap<String, TestRun>());

    @Override
    public List<TestRun> findAllTestRuns() {
        log.debug("TestRunServer: findAllTestRuns()");
        return new ArrayList<TestRun>(cache.values());
    }

    @Override
    public TestRun findTestRunById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    @Override
    public TestRun findTestRunByUuid(String uuid) {
        log.debug("TestRunServer: findTestRunByUuid()");
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    @Override
    public TestRun createTestRun() {
        log.debug("TestRunServer: createTestRun()");
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String classname = elements[1].getClassName();
        int idx = classname.lastIndexOf('.');
        if (idx > 0) {
            classname = classname.substring(idx + 1);
        }
        final String name = String.format("%s#%s", classname, elements[1].getMethodName());
        return createTestRun(name);
    }

    @Override
    public TestRun createTestRun(String name) {
        log.debug("TestRunServer: createTestRun()");
        if (name == null || name.isEmpty()) {
            return createTestRun();
        }

        final TestRun testRun = new TestRun();
        testRun.setName(name);
        testRun.setUuid(UUID.randomUUID().toString());
        cache.put(testRun.getUuid(), testRun);
        return testRun;
    }

    @Override
    public void deleteTestRun(String uuid) {
        log.debug("TestRunServer: deleteTestRun()");
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}
