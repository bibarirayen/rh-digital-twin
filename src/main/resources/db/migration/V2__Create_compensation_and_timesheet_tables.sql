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
