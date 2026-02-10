CREATE TABLE IF NOT EXISTS absence (
  id BIGSERIAL PRIMARY KEY,
  type VARCHAR(255),
  date_debut DATE,
  date_fin DATE,
  justifie BOOLEAN,
  employee_id BIGINT REFERENCES employee(id)
);
