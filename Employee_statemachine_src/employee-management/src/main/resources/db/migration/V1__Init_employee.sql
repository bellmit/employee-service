create sequence hibernate_sequence start 1 increment 1;
create table action
(
   id int8 not null,
   name varchar (255),
   spel varchar (255),
   primary key (id)
);
create table deferred_events
(
   jpa_repository_state_id int8 not null,
   deferred_events varchar (255)
);
create table guard
(
   id int8 not null,
   name varchar (255),
   spel varchar (255),
   primary key (id)
);
create table state
(
   id int8 not null,
   initial_state boolean,
   kind int4,
   machine_id varchar (255),
   region varchar (255),
   state varchar (255),
   submachine_id varchar (255),
   initial_action_id int8,
   parent_state_id int8,
   primary key (id)
);
create table state_entry_actions
(
   jpa_repository_state_id int8 not null,
   entry_actions_id int8 not null,
   primary key
   (
      jpa_repository_state_id,
      entry_actions_id
   )
);
create table state_exit_actions
(
   jpa_repository_state_id int8 not null,
   exit_actions_id int8 not null,
   primary key
   (
      jpa_repository_state_id,
      exit_actions_id
   )
);
create table state_machine
(
   machine_id varchar (255) not null,
   state varchar (255),
   state_machine_context oid,
   primary key (machine_id)
);
create table state_state_actions
(
   jpa_repository_state_id int8 not null,
   state_actions_id int8 not null,
   primary key
   (
      jpa_repository_state_id,
      state_actions_id
   )
);
create table transition
(
   id int8 not null,
   event varchar (255),
   kind int4,
   machine_id varchar (255),
   guard_id int8,
   source_id int8,
   target_id int8,
   primary key (id)
);
create table transition_actions
(
   jpa_repository_transition_id int8 not null,
   actions_id int8 not null,
   primary key
   (
      jpa_repository_transition_id,
      actions_id
   )
);
alter table deferred_events add constraint fk_state_deferred_events foreign key (jpa_repository_state_id) references state;
alter table state add constraint fk_state_initial_action foreign key (initial_action_id) references action;
alter table state add constraint fk_state_parent_state foreign key (parent_state_id) references state;
alter table state_entry_actions add constraint fk_state_entry_actions_a foreign key (entry_actions_id) references action;
alter table state_entry_actions add constraint fk_state_entry_actions_s foreign key (jpa_repository_state_id) references state;
alter table state_exit_actions add constraint fk_state_exit_actions_a foreign key (exit_actions_id) references action;
alter table state_exit_actions add constraint fk_state_exit_actions_s foreign key (jpa_repository_state_id) references state;
alter table state_state_actions add constraint fk_state_state_actions_a foreign key (state_actions_id) references action;
alter table state_state_actions add constraint fk_state_state_actions_s foreign key (jpa_repository_state_id) references state;
alter table transition add constraint fk_transition_guard foreign key (guard_id) references guard;
alter table transition add constraint fk_transition_source foreign key (source_id) references state;
alter table transition add constraint fk_transition_target foreign key (target_id) references state;
alter table transition_actions add constraint fk_transition_actions_a foreign key (actions_id) references action;
alter table transition_actions add constraint fk_transition_actions_t foreign key (jpa_repository_transition_id) references transition;

CREATE TABLE employee
(
   id SERIAL PRIMARY KEY,
   emp_code VARCHAR UNIQUE NOT NULL,
   first_name VARCHAR NOT NULL,
   last_name VARCHAR NOT NULL,
   email VARCHAR NOT NULL,
   mobile_phone VARCHAR NOT NULL,
   birth_date DATE NOT NULL,
   hire_date DATE NOT NULL,
   gender VARCHAR NOT NULL,
   current_state VARCHAR NOT NULL,
   state_machine_id VARCHAR NOT NULL
);