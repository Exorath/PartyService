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

import com.exorath.service.party.Service;
import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;

import java.util.List;
import java.util.UUID;

public class SimpleService implements Service {

    private DatabaseProvider provider;

    public SimpleService(DatabaseProvider provider) {
        this.provider = provider;
    }

    /**
     * Returns a party with matching uuid
     * If there are more than 1 party with that uuid then the party with the last expire time will be returnd.
     *
     * @param uuid UUID of the party
     * @return The party with matching uuid and last expire time
     */
    @Override
    public Party getPartyFromID(String uuid) {
        return getParty(provider.getPartyFromId(uuid));
    }

    /**
     * Return the party with the owner uuid matching the given argument
     * If more than 1 party is returned then the party with the last expire time is returned
     *
     * @param uuid UUID of the party owner
     * @return The party with owner of the uuid and last expire time
     */
    @Override
    public Party getPartyFromOwner(String uuid) {
        return getParty(provider.getPartyFromOwner(uuid));
    }

    /**
     * Get the party that expires last as this will
     * often be the party we want as it will be the newest.
     *
     * @param parties Array of parties returned from the database provider
     * @return The party that expires last, or an empty party in the case that it expired or is null
     */
    private Party getParty(List<Party> parties) {
        Party party = null;
        if (parties.size() > 1) { // Is there more than 1 party returned?
            System.out.println("Party size more than 1!");
            Party partyLastExpire = null; // Holds the item with the highest expire time
            long high = 0L; // What is the current highest expire time?
            for (Party partyScan : parties) {
                if (partyScan.getExpiry() != null && partyScan.getExpiry() > high) { // Has  expire and is highest found?
                    if (partyLastExpire != null) {
                        if (partyLastExpire.getExpiry() < System.currentTimeMillis()) {
                            // Remove the party we found if it has expired, keep the size of the db down
                            provider.removeParty(partyLastExpire.getPartyUuid());
                        }
                    }
                    partyLastExpire = partyScan; // Set party
                    high = partyScan.getExpiry(); // Update highest expire time
                }
            }
            party = partyLastExpire; // Set main party to the last expire time party
        } else if (parties.size() == 1) {
            party = parties.get(0);
        }
        if (party == null) {
            party = new Party();
        }
        if (party.getExpiry() != null && party.getExpiry() < System.currentTimeMillis()) {
            party = new Party();
        }
        return party;
    }

    /**
     * Update a party from its party uuid.
     * This will fail is the party id is null.
     * This should be the preferred over using the owner uuid!
     *
     * @param party The party that should be updated
     * @return Success false with error if something is wrong with the update, true if everything is fine
     */
    @Override
    public Success updatePartyFromId(Party party) {
        Success generalChecks = runGeneralChecks(party);
        if (!generalChecks.getSuccess()) {
            return generalChecks;
        }
        return provider.updateParty(party);
    }

    /**
     * Update a party from the owner id
     * The party does not require having a party id as one will be given to it if null
     *
     * @param party The party that should be updated, does not require a party id
     * @return Success false with error if something is wrong with the udpate, true if everything is fine
     */
    @Override
    public Success updatePartyFromOwner(Party party) {
        if (party.getPartyUuid() == null) {
            party.setPartyUuid(UUID.randomUUID().toString());
        }
        Success generalChecks = runGeneralChecks(party);
        if (!generalChecks.getSuccess()) {
            return generalChecks;
        }
        return provider.updateParty(party);
    }

    /**
     * Run general checks that should be ran on all parties before updating them
     *
     * @param party The party to run general checks on
     * @return Success false with error if there is an error, true if everything is fine
     */
    private Success runGeneralChecks(Party party) {
        if (party.getMembers() == null) {
            return new Success(false, "members is null");
        }
        if (party.getMembers().size() < 1) {
            return new Success(false, "members must contain at least 1 member");
        }
        if (party.getOwnerUuid() == null) {
            return new Success(false, "owner uuid can not be null");
        }
        if (!party.getMembers().contains(party.getOwnerUuid())) {
            return new Success(false, "members must contain the owner of the party");
        }
        if (party.getServerId() == null) {
            return new Success(false, "a party must have a server id");
        }
        if (party.getExpiry() == null) {
            return new Success(false, "a party must have an expiry time");
        }
        if (party.getPartyUuid() == null) {
            return new Success(false, "party uuid can not be null");
        }
        return new Success(true);
    }

}
