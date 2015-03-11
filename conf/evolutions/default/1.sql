# --- !Ups
create table "bookings" (
	"id" bigserial not null,
	"creationDate" timestamp with time zone not null,
	"lastModified" timestamp with time zone not null,
	"dateTime" timestamp with time zone not null,
	"court" smallint not null,
	"status" integer not null);

alter table "bookings" add constraint "pk_bookings" primary key ("id");

create table "comments" (
	"id" bigserial not null,
	"creationDate" timestamp with time zone not null,
	"text" text not null,
	"bookingId" bigint not null);
	
alter table "comments" add constraint "pk_comments" primary key ("id");
alter table "comments" add constraint "fk_comments_bookings_id" foreign key ("bookingId") references "bookings"("id") on delete cascade on update cascade;

# --- !Downs
alter table "bookings" drop constraint "pk_bookings";
drop table "bookings";

alter table "comments" drop constraint "pk_comments";
alter table "comments" drop constraint "fk_comments_bookings_id";
drop table "comments";
