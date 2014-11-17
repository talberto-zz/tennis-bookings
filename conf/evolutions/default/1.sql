# --- !Ups
create table "requests" (
	"id" serial not null,
	"date" timestamp not null,
	"status" integer not null);

create unique index "idx_requests_id" on "requests" ("id");
alter table "requests" add constraint "pk_requests" primary key using index "idx_requests_id";

# --- !Downs
alter table "requests" drop constraint "pk_requests";
drop table "requests";