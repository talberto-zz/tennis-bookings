# --- !Ups
create table "bookings" (
	"id" BIGSERIAL NOT NULL,
	"dateTime" TIMESTAMP NOT NULL,
	"court" smallint NOT NULL,
	"status" INTEGER NOT NULL);

create unique index "idx_bookings_id" on "bookings" ("id");
alter table "bookings" add constraint "pk_bookings" primary key using index "idx_bookings_id";

# --- !Downs
alter table "bookings" drop constraint "pk_bookings";
drop table "bookings";