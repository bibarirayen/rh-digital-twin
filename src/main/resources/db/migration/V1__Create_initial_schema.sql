CREATE TABLE IF NOT EXISTS department (
  id BIGSERIAL PRIMARY KEY,
  nom VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS employee (
  id BIGSERIAL PRIMARY KEY,
  matricule VARCHAR(255) UNIQUE,
  nom VARCHAR(255),
  prenom VARCHAR(255),
  role VARCHAR(255),
  date_embauche DATE,
  email_pro VARCHAR(255) UNIQUE,
  statut VARCHAR(255),
  department_id BIGINT REFERENCES department(id)
);
CREATE TABLE IF NOT EXISTS compensation (
  id BIGSERIAL PRIMARY KEY,
  salaire_base INTEGER,
  prime INTEGER,
  date_effet DATE,
  date_fin DATE,
  type_prime VARCHAR(255),
  bonus INTEGER,
  employee_id BIGINT REFERENCES employee(id)
);

CREATE TABLE IF NOT EXISTS timesheet (
  id BIGSERIAL PRIMARY KEY,
  mois INTEGER,
  annee INTEGER,
  heures_travaillees INTEGER,
  heures_sup INTEGER,
  jours_absence INTEGER,
  employee_id BIGINT REFERENCES employee(id)
);
CREATE TABLE IF NOT EXISTS absence (
  id BIGSERIAL PRIMARY KEY,
  type VARCHAR(255),
  date_debut DATE,
  date_fin DATE,
  justifie BOOLEAN,
  employee_id BIGINT REFERENCES employee(id)
);
CREATE TABLE IF NOT EXISTS contract (
  id BIGSERIAL PRIMARY KEY,
  type_contrat VARCHAR(255),
  temps_travail VARCHAR(255),
  date_debut DATE,
  date_fin DATE,
  coefficient INTEGER,
  employee_id BIGINT REFERENCES employee(id)
);
