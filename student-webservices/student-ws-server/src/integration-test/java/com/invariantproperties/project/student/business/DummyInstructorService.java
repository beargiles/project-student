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
package com.invariantproperties.project.student.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.invariantproperties.project.student.domain.Instructor;
import com.invariantproperties.project.student.domain.TestRun;

public class DummyInstructorService implements InstructorFinderService, InstructorManagerService {
    private Map<String, Instructor> cache = Collections.synchronizedMap(new HashMap<String, Instructor>());

    @Override
    public long count() {
        return countByTestRun(null);
    }

    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        for (Instructor instructor : cache.values()) {
            if (testRun.equals(instructor.getTestRun())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Instructor> findAllInstructors() {
        return new ArrayList<Instructor>(cache.values());
    }

    @Override
    public Instructor findInstructorById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    @Override
    public Instructor findInstructorByUuid(String uuid) {
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    @Override
    public List<Instructor> findInstructorsByTestRun(TestRun testRun) {
        final List<Instructor> results = new ArrayList<Instructor>();
        for (Instructor instructor : cache.values()) {
            if (testRun.equals(instructor.getTestRun())) {
                results.add(instructor);
            }
        }
        return results;
    }

    @Override
    public Instructor findInstructorByEmailAddress(String emailAddress) {
        throw new ObjectNotFoundException("[email]");
    }

    @Override
    public Instructor createInstructor(String name, String emailAddress) {
        final Instructor instructor = new Instructor();
        instructor.setUuid(UUID.randomUUID().toString());
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);
        cache.put(instructor.getUuid(), instructor);
        return instructor;
    }

    @Override
    public Instructor createInstructorForTesting(String name, String emailAddress, TestRun testRun) {
        final Instructor instructor = createInstructor(name, emailAddress);
        instructor.setTestRun(testRun);
        return instructor;
    }

    @Override
    public Instructor updateInstructor(Instructor oldInstructor, String name, String emailAddress) {
        if (!cache.containsKey(oldInstructor.getUuid())) {
            throw new ObjectNotFoundException(oldInstructor.getUuid());
        }

        final Instructor instructor = cache.get(oldInstructor.getUuid());
        instructor.setUuid(UUID.randomUUID().toString());
        instructor.setTestRun(oldInstructor.getTestRun());
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);
        return instructor;
    }

    @Override
    public void deleteInstructor(String uuid, Integer version) {
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}
