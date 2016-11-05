# PartyService
Get party information
##Endpoints
###/parties/{party_uuid} [GET]:
####Gets information about the provided player

**Arguments**:
- party_uuid (string): The uuid of the party

**Response**: 
```json
{
"owner_uuid": "a1e99827-0c05-45d6-a822-911b425a4027",
"sid": "lobby",
"members": [
"a1e99827-0c05-45d6-a822-911b425a4027",
"ff2b9407-0844-4709-926c-a25385a9986b",
"1197d849-1a0e-41c7-9c07-ae0c0fc2e3f6"
], 
"expiry": 1478190151447
}
```
- owner_uuid(string): The UUID of the player who owns the party
- sid(string): The server id the party is currently on
- members(string array): Array of all members in the party including the owner
- expiry(num): The time (in UNIX millis) of when the party becomes invalid and considered removed

###/parties/byowner/{owner_uuid} [GET]:
####Gets information about the party

**Arguments**:
- owner_uuid (string): The uuid of the player who owns the party

**Response**: 
```json
{
"party_uuid": "7ce705a7-9394-4957-8b49-8cd2a198bf0d",
"sid": "lobby",
"members": [
"a1e99827-0c05-45d6-a822-911b425a4027",
"ff2b9407-0844-4709-926c-a25385a9986b",
"1197d849-1a0e-41c7-9c07-ae0c0fc2e3f6"
], 
"expiry": 1478190151447
}
```
- party_uuid(string): The UUID of the party
- sid(string): The server id the party is currently on
- members(string array): Array of all member uuids in the party including the owner
- expiry(num): The time (in UNIX millis) of when the party becomes invalid and considered removed

###/parties/{party_uuid} [PUT]:
####Updates information about the player
**Body**:
```json
{
"owner_uuid": "a1e99827-0c05-45d6-a822-911b425a4027",
"party_uuid": "7ce705a7-9394-4957-8b49-8cd2a198bf0d",
"sid": "lobby",
"members": [
"a1e99827-0c05-45d6-a822-911b425a4027",
"ff2b9407-0844-4709-926c-a25385a9986b",
"1197d849-1a0e-41c7-9c07-ae0c0fc2e3f6"
], 
"expiry": 1478190151447
}
```

**Arguments**:
- owner_uuid(string)[OPTIONAL]: The UUID of the player who owns the party (Will become first member
- party_uuid(string): The UUID of the party
- sid(string): The server id the party is currently on
- members(string array): Array of all member uuids in the party including the owner
- expiry(num): The time (in UNIX millis) of when the party becomes invalid and considered removed

**Response**: 
```json
{"success": true}
```
- success (boolean): Whether or not the record was updated successfully.
- err (string)[OPTIONAL]: Error message only responded when the put was not successful.

###/parties/byowner/{party_uuid} [PUT]:
####Updates information about the player
**Body**:
```json
{
"owner_uuid": "a1e99827-0c05-45d6-a822-911b425a4027",
"party_uuid": "7ce705a7-9394-4957-8b49-8cd2a198bf0d",
"sid": "lobby",
"members": [
"a1e99827-0c05-45d6-a822-911b425a4027",
"ff2b9407-0844-4709-926c-a25385a9986b",
"1197d849-1a0e-41c7-9c07-ae0c0fc2e3f6"
], 
"expiry": 1478190151447
}
```

**Arguments**:
- owner_uuid(string): The UUID of the player who owns the party
- party_uuid(string)[OPTIONAL]: The UUID of the party
- sid(string): The server id the party is currently on
- members(string array): Array of all member uuids in the party including the owner
- expiry(num): The time (in UNIX millis) of when the party becomes invalid and considered removed

**Response**: 
```json
{"success": true}
```
- success (boolean): Whether or not the record was updated successfully.
- err (string)[OPTIONAL]: Error message only responded when the put was not successful.

##Environment
| Name | Value |
| --------- | --- |
| AWS_REGION | EU_CENTRAL_1 |
| AWS_ACCESS_KEY_ID	| {acces_key_id} |
| AWS_SECRET_KEY	| {secret_key} |
| PORT	| {PORT} |
| TABLE_NAME | {table_name} |
