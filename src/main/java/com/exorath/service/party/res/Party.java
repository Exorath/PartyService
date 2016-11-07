package com.exorath.service.party.res;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Party {

    private String party_uuid;
    private String owner_uuid;
    @SerializedName("sid")
    private String serverId;
    private ArrayList<String> members;
    private Long expiry;

    public Party(String party_uuid, String owner_uuid, String serverId, ArrayList<String> members, Long expiry) {
        this.party_uuid = party_uuid;
        this.owner_uuid = owner_uuid;
        this.serverId = serverId;
        this.members = members;
        this.expiry = expiry;
    }

    public Party() {}

    public String getPartyUuid() {
        return party_uuid;
    }

    public void setPartyUuid(String party_uuid) {
        this.party_uuid = party_uuid;
    }

    public String getOwnerUuid() {
        return owner_uuid;
    }

    public void setOwnerUuid(String owner_uuid) {
        this.owner_uuid = owner_uuid;
    }

    public String getServerId() {
        return serverId;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public Long getExpiry() {
        return expiry;
    }

}
