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

package com.invariantproperties.project.student.maintenance.service.impl;

import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.CourseManagerRestClient;
import com.invariantproperties.project.student.webservice.client.impl.CourseManagerRestClientImpl;

/**
 * Implementation of CourseManagerService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseManagerServiceTapestryImpl implements CourseManagerService {
    private final CourseManagerRestClient manager;

    public CourseManagerServiceTapestryImpl() {
        // resource should be loaded as tapestry resource
        final String resource = "http://localhost:8080/student-ws-webapp/rest/course/";
        manager = new CourseManagerRestClientImpl(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course createCourse(String code, String name, String summary, String description, Integer creditHours) {
        final Course actual = manager.createCourse(code, name, summary, description, creditHours);
        return actual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course updateCourse(Course course, String name, String summary, String description, Integer creditHours) {
        final Course actual = manager.updateCourse(course.getUuid(), name, summary, description, creditHours);
        return actual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCourse(String uuid, Integer version) {
        manager.deleteCourse(uuid, version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course createCourseForTesting(String code, String name, String summary, String description,
            Integer creditHours, TestRun testRun) {
        final Course actual = manager.createCourseForTesting(code, name, summary, description, creditHours, testRun);
        return actual;
    }
}
