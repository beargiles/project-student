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
package com.invariantproperties.project.student.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.invariantproperties.project.student.util.StudentUtil;

/**
 * Tests for student utilities.
 * 
 * @author bgiles
 */
public class StudentUtilTest {

    @Test
    public void testValidUuid() {
        assertTrue(StudentUtil.isPossibleUuid("63c7d688-705c-4374-937c-6628952b41e1"));
    }

    @Test
    public void testInvalidUuid() {
        assertTrue(!StudentUtil.isPossibleUuid("63c7d68x-705c-4374-937c-6628952b41e1"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d68-8705c-4374-937c-6628952b41e1"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d688-705c4-374-937c-6628952b41e1"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d688-705c-43749-37c-6628952b41e1"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d688-705c-4374-937c6-628952b41e1"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d688-705c-4374-937c-6628952b41e1a"));
        assertTrue(!StudentUtil.isPossibleUuid("63c7d688-705c-4374-937c-6628952b41e"));
        assertTrue(!StudentUtil.isPossibleUuid(""));
        assertTrue(!StudentUtil.isPossibleUuid(null));
    }
}
