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

import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;

import java.util.ArrayList;
import java.util.List;

class MemDBProvider implements DatabaseProvider {

    private ArrayList<Party> parties;

    MemDBProvider() {
        parties = new ArrayList<>();
    }

    @Override
    public List<Party> getPartyFromId(String uuid) {
        ArrayList<Party> rtn = new ArrayList<>();
        for (Party party : parties) {
            if (party.getPartyUuid().equals(uuid)) {
                rtn.add(party);
            }
        }
        return rtn;
    }

    @Override
    public List<Party> getPartyFromOwner(String uuid) {
        ArrayList<Party> rtn = new ArrayList<>();
        for (Party party : parties) {
            if (party.getOwnerUuid().equals(uuid)) {
                rtn.add(party);
            }
        }
        return rtn;
    }

    @Override
    public Success updateParty(Party party) {
        if (getPartyFromId(party.getPartyUuid()).size() == 0) {
            parties.add(party);
        } else {
            int index = 0;
            for (Party party1 : parties) {
                index++;
                if (party1.getPartyUuid().equals(party.getPartyUuid())) {
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
        if (parties.contains(uuid))
            parties.remove(uuid);
    }

}
