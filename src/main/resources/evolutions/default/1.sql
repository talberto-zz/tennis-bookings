# --- !Ups
create table "reservations" (
	"id" bigserial not null,
	"creationDate" timestamp with time zone not null,
	"lastModified" timestamp with time zone not null,
	"dateTime" timestamp with time zone not null,
	"court" smallint not null,
	"status" integer not null);

alter table "reservations" add constraint "pk_reservations" primary key ("id");

create table "reservations_events" (
	"id" bigserial not null,
	"reservation_id" varchar not null,
	"event" text not null);

alter table "reservations_events" add constraint "pk_reservations_events" primary key ("id");

# --- !Downs
alter table "reservations" drop constraint "pk_reservations";
drop table "reservations";

alter table "reservations_events" drop constraint "pk_reservations_events";
drop table "reservations_events";
