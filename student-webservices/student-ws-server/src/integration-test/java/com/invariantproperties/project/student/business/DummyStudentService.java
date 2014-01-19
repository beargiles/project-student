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

import com.invariantproperties.project.student.domain.Student;
import com.invariantproperties.project.student.domain.TestRun;

public class DummyStudentService implements StudentFinderService, StudentManagerService {
    private Map<String, Student> cache = Collections.synchronizedMap(new HashMap<String, Student>());

    @Override
    public long count() {
        return countByTestRun(null);
    }

    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        for (Student classroom : cache.values()) {
            if (testRun.equals(classroom.getTestRun())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Student> findAllStudents() {
        return new ArrayList<Student>(cache.values());
    }

    @Override
    public Student findStudentById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    @Override
    public Student findStudentByUuid(String uuid) {
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    @Override
    public List<Student> findStudentsByTestRun(TestRun testRun) {
        final List<Student> results = new ArrayList<Student>();
        for (Student student : cache.values()) {
            if (testRun.equals(student.getTestRun())) {
                results.add(student);
            }
        }
        return results;
    }

    @Override
    public Student findStudentByEmailAddress(String emailAddress) {
        throw new ObjectNotFoundException("[email]");
    }

    @Override
    public Student createStudent(String name, String emailAddress) {
        final Student student = new Student();
        student.setUuid(UUID.randomUUID().toString());
        student.setName(name);
        student.setEmailAddress(emailAddress);
        cache.put(student.getUuid(), student);
        return student;
    }

    @Override
    public Student createStudentForTesting(String name, String emailAddress, TestRun testRun) {
        final Student student = createStudent(name, emailAddress);
        student.setTestRun(testRun);
        return student;
    }

    @Override
    public Student updateStudent(Student oldStudent, String name, String emailAddress) {
        if (!cache.containsKey(oldStudent.getUuid())) {
            throw new ObjectNotFoundException(oldStudent.getUuid());
        }

        final Student student = cache.get(oldStudent.getUuid());
        student.setUuid(UUID.randomUUID().toString());
        student.setTestRun(oldStudent.getTestRun());
        student.setName(name);
        student.setEmailAddress(emailAddress);
        return student;
    }

    @Override
    public void deleteStudent(String uuid, Integer version) {
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}
