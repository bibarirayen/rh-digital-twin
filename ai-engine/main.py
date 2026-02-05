
from fastapi import FastAPI
from fastapi.responses import HTMLResponse
import pandas as pd
import json
import os
import requests
app = FastAPI(title="Digital Twin AI Engine")

@app.get("/")
def root():
    return {"message": "Welcome to Digital Twin AI Engine"}

@app.get("/analyze", response_class=HTMLResponse)
def analyze():

    try:
        employees_response = requests.get("http://localhost:8080/employees")
        employees = employees_response.json()
        salaries_response = requests.get("http://localhost:8080/salaries")
        salaries = salaries_response.json()
    except requests.exceptions.RequestException as e:
        return f"<h1>Error fetching data from API: {str(e)}</h1>"

    if not employees:
        return "<h1>No employees data available</h1>"

    if not salaries:
        # Show employees table without salary analysis
        df_emp = pd.json_normalize(employees)
        html = f"""
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>RH Digital Twin AI Analysis</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <style>
                body {{ background-color: #f8f9fa; }}
                .container {{ margin-top: 50px; }}
                .card {{ box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }}
            </style>
        </head>
        <body>
            <div class="container">
                <div class="row">
                    <div class="col-12">
                        <h1 class="text-center mb-4">RH Digital Twin AI Analysis</h1>
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5>Summary</h5>
                            </div>
                            <div class="card-body">
                                <p><strong>Total Employees:</strong> {len(df_emp)}</p>
                                <p><strong>Note:</strong> No salary data available for analysis.</p>
                            </div>
                        </div>
                        <div class="card">
                            <div class="card-header">
                                <h5>Employee List</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Name</th>
                                                <th>Role</th>
                                                <th>Department</th>
                                                <th>Date Embauche</th>
                                            </tr>
                                        </thead>
                                        <tbody>
        """
        for _, row in df_emp.iterrows():
            dept_name = row.get("department.nom", "N/A") if pd.notna(row.get("department.nom")) else "N/A"
            date_emb = str(row.get("dateEmbauche", "N/A")) if pd.notna(row.get("dateEmbauche")) else "N/A"
            html += f"""
                                            <tr>
                                                <td>{int(row['id'])}</td>
                                                <td>{row['prenom']} {row['nom']}</td>
                                                <td>{row['role']}</td>
                                                <td>{dept_name}</td>
                                                <td>{date_emb}</td>
                                            </tr>
            """
        html += """
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
        </body>
        </html>
        """
        return html

    df_emp = pd.json_normalize(employees)
    df_sal = pd.json_normalize(salaries)

    df = df_sal.merge(df_emp, left_on="employee.id", right_on="id")

    avg_salary = df.groupby("employee.department.id")["salaireBase"].mean()

    results = []

    for _, row in df.iterrows():
        emp_id = row["employee.id"]
        emp_name = f"{row['employee.prenom']} {row['employee.nom']}"
        dept_id = row["employee.department.id"]
        dept_name = row["employee.department.nom"]
        salary = row["salaireBase"]
        avg = avg_salary[dept_id]

        diff = (salary - avg) / avg

        results.append({
            "employeeId": int(emp_id),
            "employeeName": emp_name,
            "department": dept_name,
            "salary": salary,
            "departmentAverage": round(avg, 2),
            "risk": "HIGH" if abs(diff) > 0.2 else "NORMAL",
            "score": round(abs(diff), 2),
            "reason": f"{int(diff*100)}% difference from department average"
        })

    summary = {"totalEmployees": len(results), "highRiskCount": sum(1 for r in results if r["risk"] == "HIGH")}

    
    html = f"""
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>RH Digital Twin AI Analysis</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {{ background-color: #f8f9fa; }}
            .container {{ margin-top: 50px; }}
            .card {{ box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }}
            .high-risk {{ background-color: #f8d7da; }}
            .normal-risk {{ background-color: #d1ecf1; }}
        </style>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <h1 class="text-center mb-4">RH Digital Twin AI Analysis</h1>
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5>Summary</h5>
                        </div>
                        <div class="card-body">
                            <p><strong>Total Employees:</strong> {summary['totalEmployees']}</p>
                            <p><strong>High Risk Count:</strong> {summary['highRiskCount']}</p>
                        </div>
                    </div>
                    <div class="card">
                        <div class="card-header">
                            <h5>Employee Analysis</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Department</th>
                                            <th>Salary</th>
                                            <th>Dept Avg</th>
                                            <th>Risk</th>
                                            <th>Score</th>
                                            <th>Reason</th>
                                        </tr>
                                    </thead>
                                    <tbody>
    """

    for result in results:
        risk_class = "high-risk" if result["risk"] == "HIGH" else "normal-risk"
        html += f"""
                                        <tr class="{risk_class}">
                                            <td>{result['employeeId']}</td>
                                            <td>{result['employeeName']}</td>
                                            <td>{result['department']}</td>
                                            <td>${result['salary']:,.2f}</td>
                                            <td>${result['departmentAverage']:,.2f}</td>
                                            <td><span class="badge bg-{'danger' if result['risk'] == 'HIGH' else 'info'}">{result['risk']}</span></td>
                                            <td>{result['score']}</td>
                                            <td>{result['reason']}</td>
                                        </tr>
        """

    html += """
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
    </html>
    """

    return html
 