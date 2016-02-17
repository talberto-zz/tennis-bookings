# --- !Ups
create table "reservations" (
	"id" bigserial not null,
	"creationDate" timestamp with time zone not null,
	"lastModified" timestamp with time zone not null,
	"dateTime" timestamp with time zone not null,
	"court" smallint not null,
	"status" integer not null);

alter table "reservations" add constraint "pk_reservations" primary key ("id");

create table "comments" (
	"id" bigserial not null,
	"creationDate" timestamp with time zone not null,
	"text" text not null,
	"screenshot" varchar(40),
	"bookingId" bigint not null);
	
alter table "comments" add constraint "pk_comments" primary key ("id");
alter table "comments" add constraint "fk_comments_reservations_id" foreign key ("bookingId") references "reservations"("id") on delete cascade on update cascade;

# --- !Downs
alter table "reservations" drop constraint "pk_reservations";
drop table "reservations";

alter table "comments" drop constraint "pk_comments";
alter table "comments" drop constraint "fk_comments_reservations_id";
drop table "comments";
