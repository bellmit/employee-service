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
   business_key VARCHAR UNIQUE,
   current_state VARCHAR NOT NULL
);
CREATE TABLE employee_status
(
   code VARCHAR PRIMARY KEY,
   name VARCHAR NOT NULL
);
CREATE TABLE employee_event
(
   id bigserial PRIMARY KEY,
   employee_id INT REFERENCES employee (id),
   status VARCHAR REFERENCES employee_status (code),
   creation_date TIMESTAMP NOT NULL,
   description VARCHAR
);
INSERT INTO employee_status VALUES
(
   'ADDED',
   'Employee added'
);
INSERT INTO employee_status VALUES
(
   'INCHECK',
   'Employee Checked'
);
INSERT INTO employee_status VALUES
(
   'APPROVED',
   'Emlpoyee Approved'
);
INSERT INTO employee_status VALUES
(
   'ACTIVE',
   'Employee Activated'
);