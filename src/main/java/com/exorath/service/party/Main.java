/*
 * Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.service.party;

import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.commons.portProvider.PortProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorath.service.party.service.DynamoDatabaseProvider;
import com.exorath.service.party.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() throws Exception {
        DynamoDBProvider dbProvider = DynamoDBProvider.getEnvironmentDynamoDBProvider();
        TableNameProvider tableNameProvider = TableNameProvider.getEnvironmentTableNameProvider();
        Service svc = new SimpleService(new DynamoDatabaseProvider(dbProvider, tableNameProvider));
        LOG.info("Service " + svc.getClass() + " instantiated");

        Transport.setup(svc, PortProvider.getEnvironmentPortProvider());
        LOG.info("HTTP transport setup");
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
