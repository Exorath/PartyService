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

import java.util.List;

interface DatabaseProvider {

    /**
     * Get a party from the party unique id
     *
     * @param uuid Party unique id
     * @return Party object
     */
    List<Party> getPartyFromId(String uuid);

    /**
     * Get a party that is owned by a player
     *
     * @param uuid Party owner uuid
     * @return Party object
     */
    List<Party> getPartyFromOwner(String uuid);

    List<Party> getPartyFromMember(String uuid);

    /**
     * Update a party
     *
     * @param party The party that should be updated
     * @return Success object
     */
    Success updateParty(Party party);

    /**
     * Remove a party from its id
     *
     * @param uuid The party unique id
     */
    void removeParty(String uuid);

}
