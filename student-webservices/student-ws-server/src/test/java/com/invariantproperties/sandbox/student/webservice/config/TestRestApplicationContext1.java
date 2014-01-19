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
package com.invariantproperties.project.student.webservice.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import com.invariantproperties.project.student.business.ClassroomFinderService;
import com.invariantproperties.project.student.business.ClassroomManagerService;
import com.invariantproperties.project.student.business.CourseFinderService;
import com.invariantproperties.project.student.business.CourseManagerService;
import com.invariantproperties.project.student.business.InstructorFinderService;
import com.invariantproperties.project.student.business.InstructorManagerService;
import com.invariantproperties.project.student.business.SectionFinderService;
import com.invariantproperties.project.student.business.SectionManagerService;
import com.invariantproperties.project.student.business.StudentFinderService;
import com.invariantproperties.project.student.business.StudentManagerService;
import com.invariantproperties.project.student.business.TermFinderService;
import com.invariantproperties.project.student.business.TermManagerService;
import com.invariantproperties.project.student.business.TestRunService;

@Configuration
@ComponentScan(basePackages = { "com.invariantproperties.project.student.webservice.server.rest" })
@ImportResource({ "classpath:applicationContext-rest.xml" })
// @PropertySource("classpath:application.properties")
public class TestRestApplicationContext1 {

    @Resource
    private Environment environment;

    @Bean
    public ClassroomFinderService classroomFinderService() {
        return null;
    }

    @Bean
    public ClassroomManagerService classroomManagerService() {
        return null;
    }

    @Bean
    public CourseFinderService courseFinderService() {
        return null;
    }

    @Bean
    public CourseManagerService courseManagerService() {
        return null;
    }

    @Bean
    public InstructorFinderService instructorFinderService() {
        return null;
    }

    @Bean
    public InstructorManagerService instructorManagerService() {
        return null;
    }

    @Bean
    public SectionFinderService sectionFinderService() {
        return null;
    }

    @Bean
    public SectionManagerService sectionManagerService() {
        return null;
    }

    @Bean
    public StudentFinderService studentFinderService() {
        return null;
    }

    @Bean
    public StudentManagerService studentManagerService() {
        return null;
    }

    @Bean
    public TermFinderService termFinderService() {
        return null;
    }

    @Bean
    public TermManagerService termManagerService() {
        return null;
    }

    @Bean
    public TestRunService testRunService() {
        return null;
    }
}
