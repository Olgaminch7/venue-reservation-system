SELECT category_venue.venue_id, category_venue.category_id, category.name FROM category
JOIN category_venue ON category_venue.category_id = category.id
WHERE category_venue.venue_id = ?;

SELECT * FROM reservation;

START TRANSACTION;
INSERT INTO category (id, name) VALUES (10, 'TestName');
ROLLBACK;

INSERT INTO city (id, name, state_abbreviation) VALUES (DEFAULT, 'TestCity', 'OH') RETURNING id;

SELECT * FROM category;

INSERT INTO city (id, name, state_abbreviation) VALUES (13, 'TestName', 'OH');

INSERT INTO category_venue (venue_id, category_id) VALUES (?, ?);

SELECT venue.id, venue.name, venue.city_id as city_id, city.name as city_name, state.abbreviation, state.name as state_name, description 
FROM venue
JOIN city ON city.id = venue.city_id
JOIN state ON state.abbreviation = city.state_abbreviation
ORDER BY venue.name ASC;



INSERT INTO state (abbreviation, name) VALUES ('OH', 'TestState');

SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate, max_occupancy 
FROM space
WHERE venue_id = ?
LIMIT 5;

SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for 
FROM reservation
WHERE space_id = ?;

SELECT count(*) = 0 as allow_reservation
FROM reservation
WHERE space_id = ? AND (start_date, end_date) overlaps (?, ?);

INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) VALUES (nextval('reservation_reservation_id_seq'::regclass), 5, 100, '2021-02-16', '2021-02-20', 'Olga');

INSERT INTO space (id, venue_id, name, is_accessible, open_from, open_to, daily_rate, max_occupancy) VALUES (0, 0, '', , 0, 0, '', 0);

INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for) VALUES (0, 0, 0, '', '', '');

SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for 
FROM reservation
WHERE reservation_id = ?;

SELECT count(*) = 0 as space_closed
FROM space
WHERE (id = ?)
AND
(? BETWEEN open_from and open_to OR open_from IS NULL OR open_to IS NULL)
GROUP BY id;

SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::numeric, max_occupancy FROM space;

SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate, max_occupancy
FROM space
WHERE max_occupancy >= ?
AND (? BETWEEN open_from and open_to OR open_from IS NULL OR open_to IS NULL);

SELECT space.id, space.venue_id, space.name, venue.name, space.is_accessible, space.open_from, space.open_to, space.daily_rate::numeric, space.max_occupancy
FROM space
JOIN venue ON venue.id = space.venue_id
FULL OUTER JOIN category_venue ON category_venue.venue_id = venue.id
WHERE category_venue.category_id = ?
AND space.max_occupancy >= ?
AND (? BETWEEN space.open_from and space.open_to OR space.open_from IS NULL OR space.open_to IS NULL)
AND space.is_accessible = ?
AND space.daily_rate::numeric <= ?;




SELECT venue.name as venue_name, space.name as space_name, reserved_for, start_date, end_date 
FROM reservation
JOIN space ON space.id = reservation.space_id
JOIN venue ON venue.id = space.id
WHERE start_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30
ORDER BY start_date ASC;


START TRANSACTION;
INSERT INTO space (id, venue_id, name, is_accessible, open_from, open_to , cast(daily_rate as money), max_occupancy) VALUES (DEFAULT, 8, 'TestSpace', false, 2, 8, 250.00, 100);
ROLLBACK;
SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for FROM reservation;
