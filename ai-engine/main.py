
from fastapi import FastAPI
import pandas as pd
import requests
app = FastAPI(title="Digital Twin AI Engine")

BACKEND_URL = "http://localhost:8080"

@app.get("/")
def root():
    return {"message": "Welcome to Digital Twin AI Engine"}

@app.get("/analyze")
def analyze():
    employees = requests.get(f"{BACKEND_URL}/employees").json()
    salaries = requests.get(f"{BACKEND_URL}/salaries").json()

    if not employees or not salaries:
        return {"message": "No data"}

    df_emp = pd.json_normalize(employees)
    df_sal = pd.json_normalize(salaries)

    df = df_sal.merge(df_emp, left_on="employee.id", right_on="id")

    avg_salary = df.groupby("department.id")["salaireBase"].mean()

    results = []

    for _, row in df.iterrows():
        emp_id = row["employee.id"]
        dept_id = row["department.id"]
        salary = row["salaireBase"]
        avg = avg_salary[dept_id]

        diff = (salary - avg) / avg

        results.append({
            "employeeId": int(emp_id),
            "risk": "HIGH" if abs(diff) > 0.2 else "NORMAL",
            "score": round(abs(diff), 2),
            "reason": f"{int(diff*100)}% difference from department average"
        })

    return results
 