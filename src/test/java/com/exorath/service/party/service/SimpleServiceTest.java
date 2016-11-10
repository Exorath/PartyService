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
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class SimpleServiceTest {

    private Service service;

    @Before
    public void setup() {
        MemDBProvider dbProvider = new MemDBProvider();
        service = new SimpleService(dbProvider);
    }

    @Test
    public void testPartyReturnEmptyIfNoneExist() {
        Party party = service.getPartyFromOwner(UUID.randomUUID().toString());
        String partyJson = new Gson().toJson(party);
        assertNotSame("{}", partyJson);
    }

    @Test
    public void testPartyReturnedWhenTwoPartiesWithSameOwner() {
        // TODO: Fix
        ArrayList<String> members = new ArrayList<>();
        members.add("party-owner-uuid");
        // Expired party
        service.updatePartyFromId(new Party("expired-party", "party-owner-uuid", "lobby", members, System.currentTimeMillis() - 10000L));
        // Valid party
        service.updatePartyFromId(new Party("valid-party", "party-owner-uuid", "lobby", members, System.currentTimeMillis() + 100000L));

        Party getParty = service.getPartyFromOwner("party-owner-uuid");
        assertSame("valid-party", getParty.getPartyUuid());
    }

    @Test
    public void testPartyReturnedWhenTwoPartiesWithSameOwnerFlip() {
        ArrayList<String> members = new ArrayList<>();
        members.add("party-owner-uuid");
        // Valid party
        service.updatePartyFromId(new Party("valid-party", "party-owner-uuid", "lobby", members, System.currentTimeMillis() + 100000L));
        // Expired party
        service.updatePartyFromId(new Party("expired-party", "party-owner-uuid", "lobby", members, System.currentTimeMillis() - 10000L));

        Party getParty = service.getPartyFromOwner("party-owner-uuid");
        assertSame("valid-party", getParty.getPartyUuid());
    }

    @Test
    public void testPartyUpdateWithoutOwner() {
        ArrayList<String> members = new ArrayList<>();
        members.add("member");
        Party party = new Party("a-party-uuid", null, "hub", members, System.currentTimeMillis() + 10000L);
        Success success = service.updatePartyFromId(party);
        assertFalse(success.getSuccess());
    }

    @Test
    public void testPartyUpdateWithNullMembersList() {
        Party party = new Party("a-party-uuid", "party-owner", "hub", null, System.currentTimeMillis() + 10000L);
        Success success = service.updatePartyFromId(party);
        assertFalse(success.getSuccess());
    }

    @Test
    public void testPartyUpdateWithNullPartyId() {
        ArrayList<String> members = new ArrayList<>();
        members.add("party-owner");
        Party party = new Party(null, "party-owner", "hub", members, System.currentTimeMillis() + 10000L);
        Success success = service.updatePartyFromId(party);
        assertFalse(success.getSuccess());
    }

    @Test
    public void testGetPartyThatHasExpired() {
        ArrayList<String> members = new ArrayList<>();
        members.add("party-owner");
        Party party = new Party("party-uuid", "party-owner", "hub", members, System.currentTimeMillis() - 100000L);
        service.updatePartyFromId(party);

        Party get = service.getPartyFromID(party.getPartyUuid());
        assertNotSame("{}", new Gson().toJson(get));
    }

    @Test
    public void testUpdatePartyWithNullExpireTime() {
        ArrayList<String> members = new ArrayList<>();
        members.add("party-owner");
        Party party = new Party("party-uuid", "party-owner", "hub", members, null);
        Success success = service.updatePartyFromId(party);
        assertFalse(success.getSuccess());
    }

}
