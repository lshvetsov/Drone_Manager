insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('d40aad80-b87a-4424-9dcf-f0748d553a46', 'd027jm', 'LIGHTWEIGHT', 90, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('28b3f4ce-17ec-453d-8a01-49d8592db525', 'Z3d', 'LIGHTWEIGHT', 120, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('e5eb4064-913b-4ea6-8f33-f256f37f58bd', 'e3aVo', 'LIGHTWEIGHT', 150, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('4c63da08-5572-48fb-94f2-e7053cd0a1f4', '96jkkLM', 'LIGHTWEIGHT', 150, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('82c7c214-f91b-4ef8-9e6c-50e0c8fce062', 'nZig1mp', 'LIGHTWEIGHT', 150, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('221faf9e-fc40-4dea-9e24-871f2532eba4', 'xx2GVb', 'MIDDLEWEIGHT', 180, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('9e698e9d-f139-4ab9-9f96-91a3cb5fcfc3', 'T59O9', 'MIDDLEWEIGHT', 250, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('a0d9a275-2421-48f6-a93f-3ccd0da51960', 'q8j6it', 'MIDDLEWEIGHT', 300, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('40aca5e6-6ab8-4eba-97ba-9fc602be5a7f', '0uZ7RIAp', 'CRUISERWEIGHT', 400, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('2bb3e923-45de-477c-b1e2-4f5e2310f338', 'B7P2', 'CRUISERWEIGHT', 400, 100, 'IDLE') on conflict do nothing;
insert into DRONE (ID, SERIAL_NUMBER, MODEL, WEIGHT_LIMIT, BATTERY_CAPACITY, STATE) values ('ff31aea6-458d-4933-a14e-1359497230ce', 'wjdno', 'HEAVYWEIGHT', 500, 100, 'IDLE') on conflict do nothing;

insert into MEDICATION (ID, NAME, WEIGHT, CODE, IMAGE) values ('b43e439b-c937-4e23-bfcb-b2ceacdb068e', 'season', 20, '8in6vj', null) on conflict do nothing;
insert into MEDICATION (ID, NAME, WEIGHT, CODE, IMAGE) values ('3907fed0-b6e4-4b2d-9b92-14d82925fd46', 'into', 30, 'h09p4Ng', null) on conflict do nothing;
insert into MEDICATION (ID, NAME, WEIGHT, CODE, IMAGE) values ('4fd9d1f0-c228-4ea7-b8e4-bc8375c5fb96', 'short', 50, 'KR60', null) on conflict do nothing;
insert into MEDICATION (ID, NAME, WEIGHT, CODE, IMAGE) values ('e2b1a5f9-e05d-405a-b2f6-3834c168a50c', 'brick', 70, '1r6IR', null) on conflict do nothing;
insert into MEDICATION (ID, NAME, WEIGHT, CODE, IMAGE) values ('d7f0f33a-0b2c-4cfd-a84c-4d3ccf45e17f', 'asleep', 15, 'O9sfa8', null) on conflict do nothing;