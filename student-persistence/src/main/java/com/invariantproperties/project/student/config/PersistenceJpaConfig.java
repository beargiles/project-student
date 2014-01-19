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
package com.invariantproperties.project.student.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration for integration tests.
 * 
 * More information on naming strategy:
 * http://www.petrikainulainen.net/programming
 * /tips-and-tricks/implementing-a-custom-namingstrategy-with-hibernate/
 * 
 * More information on entity listeners:
 * http://deepintojee.wordpress.com/2012/02
 * /05/spring-managed-event-listeners-with-jpa/
 * 
 * @author bgiles
 */
@Configuration
@EnableJpaRepositories(basePackages = { "com.invariantproperties.project.student.repository" })
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ImportResource("classpath:applicationContext-dao.xml")
public class PersistenceJpaConfig {
}
