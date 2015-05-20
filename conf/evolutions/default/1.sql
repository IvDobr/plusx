# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table done_lab (
  donelab_id                integer not null,
  donelab_date              timestamp,
  donelab_stud_stud_id      integer,
  donelab_lab_lab_id        integer,
  constraint pk_done_lab primary key (donelab_id))
;

create table lab (
  lab_id                    integer not null,
  lab_title                 varchar(255),
  lab_death_line            timestamp,
  lab_group_group_id        integer,
  constraint pk_lab primary key (lab_id))
;

create table plus (
  plus_id                   integer not null,
  plus_date                 timestamp,
  plus_stud_stud_id         integer,
  constraint pk_plus primary key (plus_id))
;

create table prepod (
  prepod_id                 integer not null,
  prepod_login              varchar(255),
  prepod_first_name         varchar(255),
  prepod_last_name          varchar(255),
  prepod_pass               varchar(255),
  constraint uq_prepod_prepod_login unique (prepod_login),
  constraint pk_prepod primary key (prepod_id))
;

create table stud_group (
  group_id                  integer not null,
  group_title               varchar(255),
  gpoup_prepod_prepod_id    integer,
  constraint pk_stud_group primary key (group_id))
;

create table student (
  stud_id                   integer not null,
  stud_first_name           varchar(255),
  stud_last_name            varchar(255),
  stud_stud_group_group_id  integer,
  constraint pk_student primary key (stud_id))
;

create sequence donelab_seq;

create sequence lab_seq;

create sequence plus_seq;

create sequence prepod_seq;

create sequence studgroup_seq;

create sequence student_seq;

alter table done_lab add constraint fk_done_lab_donelabStud_1 foreign key (donelab_stud_stud_id) references student (stud_id) on delete restrict on update restrict;
create index ix_done_lab_donelabStud_1 on done_lab (donelab_stud_stud_id);
alter table done_lab add constraint fk_done_lab_donelabLab_2 foreign key (donelab_lab_lab_id) references lab (lab_id) on delete restrict on update restrict;
create index ix_done_lab_donelabLab_2 on done_lab (donelab_lab_lab_id);
alter table lab add constraint fk_lab_labGroup_3 foreign key (lab_group_group_id) references stud_group (group_id) on delete restrict on update restrict;
create index ix_lab_labGroup_3 on lab (lab_group_group_id);
alter table plus add constraint fk_plus_plusStud_4 foreign key (plus_stud_stud_id) references student (stud_id) on delete restrict on update restrict;
create index ix_plus_plusStud_4 on plus (plus_stud_stud_id);
alter table stud_group add constraint fk_stud_group_gpoupPrepod_5 foreign key (gpoup_prepod_prepod_id) references prepod (prepod_id) on delete restrict on update restrict;
create index ix_stud_group_gpoupPrepod_5 on stud_group (gpoup_prepod_prepod_id);
alter table student add constraint fk_student_studStudGroup_6 foreign key (stud_stud_group_group_id) references stud_group (group_id) on delete restrict on update restrict;
create index ix_student_studStudGroup_6 on student (stud_stud_group_group_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists done_lab;

drop table if exists lab;

drop table if exists plus;

drop table if exists prepod;

drop table if exists stud_group;

drop table if exists student;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists donelab_seq;

drop sequence if exists lab_seq;

drop sequence if exists plus_seq;

drop sequence if exists prepod_seq;

drop sequence if exists studgroup_seq;

drop sequence if exists student_seq;

