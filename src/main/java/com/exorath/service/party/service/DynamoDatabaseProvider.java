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

package com.exorath.service.party.service;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.exorath.service.commons.dynamoDBProvider.DynamoDBProvider;
import com.exorath.service.commons.tableNameProvider.TableNameProvider;
import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DynamoDatabaseProvider implements DatabaseProvider {

    private static final String OWNER_UUID = "owner_uuid", PARTY_UUID = "party_uuid", SERVER_ID = "sid", MEMBERS = "members", EXPIRE = "expiry";

    private static final Logger logger = LoggerFactory.getLogger(DynamoDatabaseProvider.class);

    private DynamoDB database;
    private Table table;
    private String tableName;

    public DynamoDatabaseProvider(DynamoDBProvider dbProvider, TableNameProvider tableNameProvider) {
        this.database = dbProvider.getDB();
        this.tableName = tableNameProvider.getTableName();
        this.table = setupTable(PARTY_UUID, OWNER_UUID);
    }

    /**
     * @param primKey Primary partition key for the table
     * @param gsi     String array for global secondary index's, allow for searching on more than primary key
     * @return Table containing party information
     */
    private Table setupTable(String primKey, String... gsi) {
        Table table;
        try {
            ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(1L, 1L);
            ArrayList<GlobalSecondaryIndex> gsiArr = new ArrayList<>();
            ArrayList<AttributeDefinition> attDefs = new ArrayList<>();
            for (String g : gsi) {
                GlobalSecondaryIndex gsiIndex = new GlobalSecondaryIndex()
                        .withIndexName(g)
                        .withProvisionedThroughput(provisionedThroughput)
                        .withKeySchema(new KeySchemaElement()
                                .withAttributeName(g)
                                .withKeyType(KeyType.HASH))
                        .withProjection(new Projection()
                                .withProjectionType("ALL"));
                gsiArr.add(gsiIndex);
                attDefs.add(new AttributeDefinition(g, ScalarAttributeType.S));
            }
            attDefs.add(new AttributeDefinition(primKey, ScalarAttributeType.S));
            table = database.createTable(new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(new KeySchemaElement(primKey, KeyType.HASH))
                    .withGlobalSecondaryIndexes(gsiArr)
                    .withAttributeDefinitions(attDefs)
                    .withProvisionedThroughput(provisionedThroughput)
            );
            logger.info("Created DynamoDB table " + tableName + " with 1r/1w provisioning. Waiting for it to activate.");
        } catch (ResourceInUseException ex) {
            table = database.getTable(tableName);
            logger.info("DynamoDB table " + tableName + " already existed. Waiting for it to activate.");
        }

        try {
            table.waitForActive();
        } catch (InterruptedException ex) {
            logger.error("DynamoDB table " + tableName + " could not activate!\n" + ex.getMessage());
            System.exit(1);
        }
        logger.info("DynamoDB table " + tableName + " active.");
        return table;
    }

    /**
     * Get all parties with the unique id provided
     *
     * @param uuid Party unique id
     * @return List of all parties that have the party id given
     */
    @Override
    public List<Party> getPartyFromId(String uuid) {
        return getParties(uuid, PARTY_UUID);
    }

    /**
     * Get all parties with the given owner uuid
     *
     * @param uuid Party owner uuid
     * @return List of all parties with the given owner uuid
     */
    @Override
    public List<Party> getPartyFromOwner(String uuid) {
        return getParties(uuid, OWNER_UUID);
    }

    /**
     * Get all parties where the key 'primKey' is 'uuid'
     * Some parties may be expired, service should check for those!
     *
     * @param uuid    The uuid to be looked for
     * @param primKey The index to search for the data
     * @return A list of all parties that matched the uuid to the primary key
     */
    private List<Party> getParties(String uuid, String primKey) {
        QuerySpec query = getQuerySpec(primKey, uuid);
        ArrayList<Item> items = new ArrayList<>();

        ItemCollection<QueryOutcome> queryOutcome;
        if (primKey.equals(PARTY_UUID)) {
            queryOutcome = table.query(query);
        } else {
            Index index = table.getIndex(primKey);
            queryOutcome = index.query(query);
        }
        for (Page<Item, QueryOutcome> page : queryOutcome.pages()) {
            for (Item aPage : page) {
                items.add(aPage);
            }
        }

        if (items.size() > 0) { // Did the query return something?
            List<Party> parties = new ArrayList<>();
            for (Item item : items) {
                parties.add(getPartyFromItem(item));
            }
            return parties;
        }
        return null; // The party must not exist
    }

    /**
     * Get the query spec for a search
     *
     * @param primKey The primary key to search with
     * @param uuid    The value that should be looked for
     * @return Query spec that will return all matching results when ran
     */
    private QuerySpec getQuerySpec(String primKey, String uuid) {
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", primKey);
        nameMap.put("#expire", EXPIRE);

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":uuid", uuid);
        valueMap.put(":expire", System.currentTimeMillis());

        return new QuerySpec()
                .withKeyConditionExpression("#id = :uuid")
                .withFilterExpression("#expire > :expire")
                .withNameMap(nameMap)
                .withValueMap(valueMap);
    }

    /**
     * Convert an item containing information about a party into a party object
     *
     * @param item The item containing the information
     * @return A party object containing all possible information from the item
     */
    private Party getPartyFromItem(Item item) {
        String owner_uuid = item.hasAttribute(OWNER_UUID) ? item.getString(OWNER_UUID) : null;
        String party_uuid = item.hasAttribute(PARTY_UUID) ? item.getString(PARTY_UUID) : null;
        String serverId = item.hasAttribute(SERVER_ID) ? item.getString(SERVER_ID) : null;
        ArrayList<String> members = item.hasAttribute(MEMBERS) ? new ArrayList<>(item.getStringSet(MEMBERS)) : null;
        Long expiry = item.hasAttribute(EXPIRE) ? item.getLong(EXPIRE) : null;
        return new Party(party_uuid, owner_uuid, serverId, members, expiry);
    }

    /**
     * Update the party in the database
     * Update will use the party_uuid, should be set before calling this!
     *
     * @param party The party that should be updated
     * @return Success true if the item updated without error
     */
    @Override
    public Success updateParty(Party party) {
        UpdateItemSpec update = getUpdateItemSpec(party);
        table.updateItem(update);
        return new Success(true);
    }

    /**
     * Get an update item spec for the given party argument
     * This does not call the update, just returns an object that can be used to update the party
     *
     * @param party Generate the update spec based on this party
     * @return Update spec with all the information to update the party
     */
    private UpdateItemSpec getUpdateItemSpec(Party party) {
        UpdateItemSpec update = new UpdateItemSpec();
        update.withPrimaryKey(new KeyAttribute(PARTY_UUID, party.getPartyUuid()));
        //update.addAttributeUpdate(new AttributeUpdate(PARTY_UUID).put(party.getPartyUuid()));
        update.addAttributeUpdate(new AttributeUpdate(OWNER_UUID).put(party.getOwnerUuid()));
        update.addAttributeUpdate(new AttributeUpdate(SERVER_ID).put(party.getServerId()));
        update.addAttributeUpdate(new AttributeUpdate(MEMBERS).put(party.getMembers()));
        update.addAttributeUpdate(new AttributeUpdate(EXPIRE).put(party.getExpiry()));
        return update;
    }

    /**
     * Removes a party from the database using its party_uuid
     *
     * @param uuid The party unique id
     */
    public void removeParty(String uuid) {
        if (getPartyFromId(uuid) != null) {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec();
            deleteItemSpec.withPrimaryKey(new KeyAttribute(PARTY_UUID, uuid));
            table.deleteItem(deleteItemSpec);
        }
    }

}
