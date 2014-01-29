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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.domain.Course;
import com.invariantproperties.project.student.maintenance.query.SortCriterion;
import com.invariantproperties.project.student.maintenance.query.SortDirection;

public class CoursePagedDataSource implements GridDataSource {

    private int startIndex;
    private List<Course> preparedResults;

    private final CourseFinderService courseFinderService;

    public CoursePagedDataSource(CourseFinderService courseFinderService) {
        this.courseFinderService = courseFinderService;
    }

    @Override
    public int getAvailableRows() {
        long count = courseFinderService.count();
        return (int) count;
    }

    @Override
    public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {

        // Get a page of courses - ask business service to find them (from the
        // database)
        // List<SortCriterion> sortCriteria = toSortCriteria(sortConstraints);
        // preparedResults = courseFinderService.findCourses(startIndex,
        // endIndex - startIndex + 1, sortCriteria);
        preparedResults = courseFinderService.findAllCourses();

        this.startIndex = startIndex;
    }

    @Override
    public Object getRowValue(final int index) {
        return preparedResults.get(index - startIndex);
    }

    @Override
    public Class<Course> getRowType() {
        return Course.class;
    }

    /**
     * Converts a list of Tapestry's SortConstraint to a list of our business
     * tier's SortCriterion. The business tier does not use SortConstraint
     * because that would create a dependency on Tapestry.
     */
    private List<SortCriterion> toSortCriteria(List<SortConstraint> sortConstraints) {
        List<SortCriterion> sortCriteria = new ArrayList<SortCriterion>();

        for (SortConstraint sortConstraint : sortConstraints) {

            String propertyName = sortConstraint.getPropertyModel().getPropertyName();
            SortDirection sortDirection = SortDirection.UNSORTED;

            switch (sortConstraint.getColumnSort()) {
            case ASCENDING:
                sortDirection = SortDirection.ASCENDING;
                break;
            case DESCENDING:
                sortDirection = SortDirection.DESCENDING;
                break;
            default:
            }

            SortCriterion sortCriterion = new SortCriterion(propertyName, sortDirection);
            sortCriteria.add(sortCriterion);
        }

        return sortCriteria;
    }
}
