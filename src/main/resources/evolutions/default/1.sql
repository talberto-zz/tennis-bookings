# --- !Ups
create table "reservations_events" (
	"id" bigserial not null,
	"reservation_id" uuid not null,
	"event" text not null);

alter table "reservations_events" add constraint "pk_reservations_events" primary key ("id");

# --- !Downs
alter table "reservations_events" drop constraint "pk_reservations_events";
drop table "reservations_events";
