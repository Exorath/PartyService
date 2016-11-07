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

import com.exorath.service.commons.portProvider.PortProvider;
import com.exorath.service.party.res.Party;
import com.exorath.service.party.res.Success;
import com.google.gson.Gson;
import spark.Route;

import static spark.Spark.*;

class Transport {
    private static final Gson GSON = new Gson();

    static void setup(Service service, PortProvider portProvider) {
        port(portProvider.getPort());
        // Party UUID
        get("/parties/:party_uuid", Transport.getGetPartyFromIdRoute(service), GSON::toJson);
        put("/parties/:party_uuid", Transport.getUpdatePartyFromIdRoute(service), GSON::toJson);
        // Owner UUID
        get("/parties/byowner/:owner_uuid", Transport.getGetPartyFromOwnerRoute(service), GSON::toJson);
        put("/parties/byowner/:owner_uuid", Transport.getUpdatePartyFromOwnerRoute(service), GSON::toJson);
        // Member UUID
        get("/parties/player/:member_uuid", Transport.getGetPartyFromMember(service), GSON::toJson);
    }

    private static Route getGetPartyFromIdRoute(Service service) {
        return (req, res) -> {
            try {
                String param = req.params("party_uuid");
                Party party = service.getPartyFromID(param);
                return party;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private static Route getGetPartyFromOwnerRoute(Service service) {
        return (req, res) -> {
            try {
                return service.getPartyFromOwner(req.params("owner_uuid"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private static Route getUpdatePartyFromIdRoute(Service service) {
        return (req, res) -> {
            Party party = null;
            try {
                party = GSON.fromJson(req.body(), Party.class);
                party.setPartyUuid(req.params("party_uuid"));
            } catch (Exception e) {
                e.printStackTrace();
                return new Success(false, "Invalid json");
            }
            try {
                return service.updatePartyFromId(party);
            } catch (Exception e) {
                e.printStackTrace();
                return new Success(false, e.getMessage());
            }
        };
    }

    private static Route getUpdatePartyFromOwnerRoute(Service service) {
        return (req, res) -> {
            Party party = null;
            try {
                party = GSON.fromJson(req.body(), Party.class);
                party.setOwnerUuid(req.params("owner_uuid"));
            } catch (Exception e) {
                e.printStackTrace();
                return new Success(false, "Invalid json");
            }
            try {
                return service.updatePartyFromOwner(party);
            } catch (Exception e) {
                e.printStackTrace();
                return new Success(false, e.getMessage());
            }
        };
    }

    private static Route getGetPartyFromMember(Service service) {
        return (req, res) -> {
            try {
                String param = req.params("member_uuid");
                Party party = service.getPartyFromMember(param);
                return party;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

}
