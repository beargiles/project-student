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
package com.invariantproperties.project.student.webservice.client.impl;

import java.util.Arrays;
import java.util.List;

import com.invariantproperties.project.student.domain.Section;
import com.invariantproperties.project.student.domain.TestRun;
import com.invariantproperties.project.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.project.student.webservice.client.SectionFinderRestClient;

/**
 * Implementation of SectionFinderRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionFinderRestClientImpl extends AbstractFinderRestClientImpl<Section> implements
        SectionFinderRestClient {
    private static final Section[] EMPTY_COURSE_ARRAY = new Section[0];

    /**
     * Constructor.
     * 
     * @param sectionResource
     */
    public SectionFinderRestClientImpl(final String resource) {
        super(resource, Section.class, Section[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> findAllSections() {
        Section[] sections = super.getAllObjects(EMPTY_COURSE_ARRAY);
        return Arrays.asList(sections);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Section findSectionById(final Integer id) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Section findSectionByUuid(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> findSectionsByTestRun(final TestRun testRun) {
        throw new UnsupportedOperationException();
    }
}
