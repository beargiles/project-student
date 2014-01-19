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

import com.invariantproperties.project.student.business.ClassroomManagerService;
import com.invariantproperties.project.student.domain.Classroom;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.ClassroomManagerRestClient;
import com.invariantproperties.project.student.webservice.client.impl.ClassroomManagerRestClientImpl;

/**
 * Implementation of ClassroomManagerService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomManagerServiceTapestryImpl implements ClassroomManagerService {
    private final ClassroomManagerRestClient manager;

    public ClassroomManagerServiceTapestryImpl() {
        // resource should be loaded as tapestry resource
        final String resource = "http://localhost:8080/student-ws-webapp/rest/classroom/";
        manager = new ClassroomManagerRestClientImpl(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom createClassroom(String name) {
        final Classroom actual = manager.createClassroom(name);
        return actual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom updateClassroom(Classroom classroom, String name) {
        final Classroom actual = manager.updateClassroom(classroom.getUuid(), name);
        return actual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteClassroom(String uuid, Integer version) {
        manager.deleteClassroom(uuid, version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Classroom createClassroomForTesting(String name, TestRun testRun) {
        final Classroom actual = manager.createClassroom(name);
        return actual;
    }
}
