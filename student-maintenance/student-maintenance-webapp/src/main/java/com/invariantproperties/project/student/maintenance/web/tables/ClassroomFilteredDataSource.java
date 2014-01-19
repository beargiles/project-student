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
package com.invariantproperties.project.student.maintenance.web.tables;

import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.domain.Classroom;

public class ClassroomFilteredDataSource implements GridDataSource {
    private ClassroomFinderService classroomFinderService;
    private String partialName;

    private int startIndex;
    private List<Classroom> preparedResults;

    public ClassroomFilteredDataSource(ClassroomFinderService classroomFinderService, String partialName) {
        this.classroomFinderService = classroomFinderService;
        this.partialName = partialName;
    }

    @Override
    public int getAvailableRows() {
        return (int) classroomFinderService.count();
    }

    @Override
    public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {
        // preparedResults = classroomFinderService.findClassrooms(partialName,
        // startIndex, endIndex - startIndex + 1);
        preparedResults = classroomFinderService.findAllClassrooms();
        this.startIndex = startIndex;
    }

    @Override
    public Object getRowValue(final int index) {
        return preparedResults.get(index - startIndex);
    }

    @Override
    public Class<Classroom> getRowType() {
        return Classroom.class;
    }

}
