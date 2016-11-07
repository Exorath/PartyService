package com.exorath.service.party.service;

import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;

import java.util.ArrayList;
import java.util.List;

public class MemDBProvider implements DatabaseProvider{

    private ArrayList<Party> parties;

    public MemDBProvider() {
        parties = new ArrayList<>();
    }

    @Override
    public List<Party> getPartyFromId(String uuid) {
        ArrayList<Party> rtn = new ArrayList<>();
        for(Party party : parties) {
            if(party.getPartyUuid().equals(uuid)) {
                rtn.add(party);
            }
        }
        return rtn;
    }

    @Override
    public List<Party> getPartyFromOwner(String uuid) {
        ArrayList<Party> rtn = new ArrayList<>();
        for(Party party : parties) {
            if(party.getOwnerUuid().equals(uuid)) {
                rtn.add(party);
            }
        }
        return rtn;
    }

    @Override
    public List<Party> getPartyFromMember(String uuid) {
        return null; //TODO
    }

    @Override
    public Success updateParty(Party party) {
        if(getPartyFromId(party.getPartyUuid()).size() == 0) {
            parties.add(party);
        } else {
            int index = 0;
            for(Party party1 : parties) {
                index++;
                if(party1.getPartyUuid().equals(party.getPartyUuid())) {
                    break;
                }
            }
            parties.remove(index);
            parties.add(index, party);
        }
        return new Success(true);
    }

    @Override
    public void removeParty(String uuid) {
        System.out.println("Removing: " + uuid);
        parties.remove(uuid);
    }

}
