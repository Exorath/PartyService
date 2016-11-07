package com.exorath.service.party.service;

import com.exorath.service.party.Service;
import com.exorath.service.party.res.Party;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertNotSame;

public class SimpleServiceTest {

    private MemDBProvider dbProvider;
    private Service service;

    @Before
    public void setup() {
        dbProvider = new MemDBProvider();
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
        assertNotSame("valid-party", getParty.getPartyUuid());
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
        System.out.println("Party UUID: " + getParty.getPartyUuid());
        assertNotSame("valid-party", getParty.getPartyUuid());
    }

}
