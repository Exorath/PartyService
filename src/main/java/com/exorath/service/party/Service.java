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

import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;

public interface Service {

    /**
     * Get party from the party uuid
     *
     * Exceptions:
     *   Database unreachable
     *
     * @param uuid UUID of the party
     * @return Party with id given, empty party if no party with that id
     */
    Party getPartyFromID(String uuid);

    /**
     * Get party from owner uuid
     *
     * Exceptions:
     *   Database unreachable
     *
     * @param uuid UUID of the party
     * @return Party that is owned by player, empty if no party
     */
    Party getPartyFromOwner(String uuid);

    /**
     * Get party from owner uuid
     *
     * Exceptions:
     *   Database unreachable
     *
     * @param uuid UUID of the party
     * @return Party that member is party of, empty is no party
     */
    Party getPartyFromMember(String uuid);

    /**
     * Update party from the party uuid
     *
     * Exceptions:
     *   Owner uuid is not in members array
     *   Members array is empty
     *   Expiry is less than current time
     *   Server is null
     *   Owner uuid is null
     *
     * @param party Party object
     * @return Returns success object
     */
    Success updatePartyFromId(Party party);

    /**
     * Update party from the owners uuid
     * Party uuid is not required, will be randomly generated if null
     *
     * Exceptions:
     *   Owner uuid is not in members array
     *   Members array is empty
     *   Expiry is less than current time
     *   Server is null
     *
     * @param party Party object
     * @return Returns success object
     */
    Success updatePartyFromOwner(Party party);

}
