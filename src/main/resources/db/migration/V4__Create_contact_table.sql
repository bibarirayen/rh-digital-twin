CREATE TABLE IF NOT EXISTS contract (
  id BIGSERIAL PRIMARY KEY,
  type_contrat VARCHAR(255),
  temps_travail VARCHAR(255),
  date_debut DATE,
  date_fin DATE,
  coefficient INTEGER,
  employee_id BIGINT REFERENCES employee(id)
);
