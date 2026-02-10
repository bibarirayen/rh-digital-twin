import React, {useState, useEffect, useMemo} from 'react'
import { apiGet, apiUpload } from '../api'

const MENU_ITEMS = [
  { id: 'overview', label: '📊 Overview', icon: '📊' },
  { id: 'employees', label: '👥 Employees', icon: '👥' },
  { id: 'departments', label: '🏢 Departments', icon: '🏢' },
  { id: 'salaries', label: '💰 Salaries', icon: '💰' },
  { id: 'timesheets', label: '⏱️ Timesheets', icon: '⏱️' },
  { id: 'absences', label: '📅 Absences', icon: '📅' },
  { id: 'contracts', label: '📋 Contracts', icon: '📋' },
]

export default function Dashboard(){
  const [view, setView] = useState('overview')
  const [cache, setCache] = useState({})
  const [loading, setLoading] = useState(false)
  const [err, setErr] = useState(null)
  const [search, setSearch] = useState('')
  const [uploading, setUploading] = useState(false)
  const [lastUploadMsg, setLastUploadMsg] = useState('')
  const [sortKey, setSortKey] = useState(null)
  const [sortDir, setSortDir] = useState(1)

  // Load data lazily based on current view
  useEffect(() => {
    loadDataForView(view)
  }, [view])

  async function loadDataForView(viewId) {
    // Don't reload if already cached
    if (cache[viewId]) return

    setLoading(true)
    setErr(null)
    try {
      const endpoints = {
        overview: ['/api/departments', '/api/employees', '/api/salaries/with-employee', '/api/timesheets/with-employee'],
        departments: ['/api/departments'],
        employees: ['/api/employees'],
        salaries: ['/api/salaries/with-employee'],
        timesheets: ['/api/timesheets/with-employee'],
        absences: ['/api/absences'],
        contracts: ['/api/contracts'],
      }

      const urls = endpoints[viewId] || []
      const results = await Promise.all(urls.map(url => apiGet(url).catch(() => [])))

      const dataMap = {
        departments: results[0],
        employees: results[1] || results[0],
        salaries: results[1] || results[0],
        timesheets: results[1] || results[0],
        absences: results[0],
        contracts: results[0],
      }

      if (viewId === 'overview') {
        setCache(prev => ({
          ...prev,
          departments: results[0] || [],
          employees: results[1] || [],
          salaries: results[2] || [],
          timesheets: results[3] || [],
        }))
      } else {
        setCache(prev => ({...prev, [viewId]: dataMap[viewId] || results[0] || []}))
      }
    } catch(e) { 
      setErr('Failed to load data. Try refreshing.')
    }
    setLoading(false)
  }

  async function handleUpload(e, endpoint){
    const file = e.target.files[0]
    if(!file) return
    setUploading(true)
    setLastUploadMsg('')
    try{
      const res = await apiUpload(endpoint, file)
      // Clear cache for this view to force reload
      setCache(prev => ({...prev, [view]: undefined}))
      await loadDataForView(view)
      setLastUploadMsg('✓ Upload succeeded: ' + (res.length || 0) + ' records')
    }catch(err){
      setLastUploadMsg('✗ Upload failed: ' + (err.message || err))
    }
    setUploading(false)
    setTimeout(()=>setLastUploadMsg(''), 5000)
  }

  function getStats(){
    return {
      totalDepts: (cache.departments || []).length,
      totalEmp: (cache.employees || []).length,
      totalSalaries: (cache.salaries || []).length,
      totalTimesheets: (cache.timesheets || []).length,
      totalAbsences: (cache.absences || []).length,
      totalContracts: (cache.contracts || []).length,
      avgSalary: (cache.salaries || []).length ? Math.round((cache.salaries || []).reduce((a,b)=>a+(b.salaireBase||0),0)/(cache.salaries || []).length) : 0,
      totalOvertime: (cache.timesheets || []).reduce((a,b)=>a+(b.heuresSup||0),0),
    }
  }

  function sortBy(key){
    if(sortKey===key) setSortDir(-sortDir)
    else { setSortKey(key); setSortDir(1) }
  }

  function sorted(list){
    if(!sortKey) return list
    return [...list].sort((a,b)=>{
      const va = (a[sortKey]===undefined||a[sortKey]===null)? '' : a[sortKey]
      const vb = (b[sortKey]===undefined||b[sortKey]===null)? '' : b[sortKey]
      if(typeof va === 'number' && typeof vb === 'number') return (va-vb)*sortDir
      return String(va).localeCompare(String(vb)) * sortDir
    })
  }

  function filterData(list){
    if(!search) return sorted(list)
    return sorted(list.filter(item=> JSON.stringify(item).toLowerCase().includes(search.toLowerCase())))
  }

  function downloadSample(type){
    const samples = {
      departments: 'nom\nEngineering\nHuman Resources\nFinance\nIT Operations\nData Science',
      employees: 'id,matricule,nom,prenom,role,date_embauche,email_pro,statut,department_id\n1,EMP001,Dupont,Jean,Développeur,2024-01-15,jean@example.com,Actif,1',
      salaries: 'id,salaire_base,prime,date_effet,type_prime,bonus,employee_id\n1,3000,200,2024-01-01,Seniority,100,1',
      timesheets: 'id,mois,heures_travaillees,heures_sup,jours_absence,employee_id\n1,1,160,10,2,1',
      absences: 'id,type,date_debut,date_fin,justifie,employee_id\n1,Vacation,2024-01-15,2024-01-19,true,1',
      contracts: 'id,type_contrat,temps_travail,date_debut,date_fin,coefficient,employee_id\n1,CDI,35,2024-01-01,,1.0,1',
    }
    const content = samples[type] || ''
    const blob = new Blob([content], {type:'text/csv'})
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url; a.download = type + '-sample.csv'; a.click()
    URL.revokeObjectURL(url)
  }

  async function refreshAll() {
    setCache({})
    await loadDataForView(view)
  }

  const stats = useMemo(getStats, [cache])

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="logo">
          <h2>RH Digital Twin</h2>
          <p>HR Management System</p>
        </div>
        <nav className="menu">
          {MENU_ITEMS.map(item=> (
            <button
              key={item.id}
              className={`menu-item ${view===item.id ? 'active' : ''}`}
              onClick={()=>{setView(item.id); setSearch('')}}
            >
              <span className="icon">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button onClick={refreshAll} className="refresh-btn">🔄 Refresh</button>
        </div>
      </aside>

      <div className="main-content">
        <header className="dash-header">
          <div>
            <h1>{MENU_ITEMS.find(m=>m.id===view)?.label || 'Dashboard'}</h1>
          </div>
          <div className="header-actions">
            {view !== 'overview' && (
              <input
                type="text"
                placeholder="Search..."
                value={search}
                onChange={e=>setSearch(e.target.value)}
                className="search-input"
              />
            )}
          </div>
        </header>

        {loading && <div className="loading">📥 Loading data...</div>}
        {err && <div className="error">⚠️ {err}</div>}
        {lastUploadMsg && <div className={lastUploadMsg.includes('✓') ? 'success' : 'error'}>{lastUploadMsg}</div>}

        <main className="content">
          {view==='overview' && (
            <div className="overview-section">
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon">👥</div>
                  <div className="stat-value">{stats.totalEmp}</div>
                  <div className="stat-label">Employees</div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">🏢</div>
                  <div className="stat-value">{stats.totalDepts}</div>
                  <div className="stat-label">Departments</div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">💰</div>
                  <div className="stat-value">{stats.totalSalaries}</div>
                  <div className="stat-label">Salary Records</div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">⏱️</div>
                  <div className="stat-value">{stats.totalTimesheets}</div>
                  <div className="stat-label">Timesheets</div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">📅</div>
                  <div className="stat-value">{stats.totalAbsences}</div>
                  <div className="stat-label">Absences</div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">📋</div>
                  <div className="stat-value">{stats.totalContracts}</div>
                  <div className="stat-label">Contracts</div>
                </div>
              </div>

              <div className="overview-details">
                <div className="detail-box">
                  <h3>💰 Average Salary</h3>
                  <div className="big-value">{stats.avgSalary} €</div>
                </div>
                <div className="detail-box">
                  <h3>⏱️ Total Overtime</h3>
                  <div className="big-value">{stats.totalOvertime} hrs</div>
                </div>
                <div className="detail-box">
                  <h3>📊 System Status</h3>
                  <div className="status-indicator">✓ All systems operational</div>
                </div>
              </div>
            </div>
          )}

          {view==='employees' && (
            <Section
              title="Employees"
              data={filterData(cache.employees || [])}
              columns={['id', 'matricule', 'nom', 'prenom', 'role', 'emailPro', 'statut']}
              columnLabels={{id:'ID', matricule:'Matricule', nom:'Name', prenom:'First Name', role:'Role', emailPro:'Email Pro', statut:'Status'}}
              uploadEndpoint="/api/employees/import"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('employees')}
            />
          )}

          {view==='departments' && (
            <Section
              title="Departments"
              data={filterData(cache.departments || [])}
              columns={['id', 'nom']}
              columnLabels={{id:'ID', nom:'Department Name'}}
              uploadEndpoint="/api/departments/upload-csv"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('departments')}
            />
          )}

          {view==='salaries' && (
            <Section
              title="Compensation"
              data={filterData(cache.salaries || [])}
              columns={['id', 'employeeName', 'salaireBase', 'prime', 'bonus', 'typePrime', 'dateEffet']}
              columnLabels={{id:'ID', employeeName:'Employee', salaireBase:'Base Salary', prime:'Prime', bonus:'Bonus', typePrime:'Type', dateEffet:'Effective Date'}}
              uploadEndpoint="/api/salaries/upload-csv"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('salaries')}
            />
          )}

          {view==='timesheets' && (
            <Section
              title="Timesheets"
              data={filterData(cache.timesheets || [])}
              columns={['id', 'employeeName', 'mois', 'heuresTravaillees', 'heuresSup', 'joursAbsence']}
              columnLabels={{id:'ID', employeeName:'Employee', mois:'Month', heuresTravaillees:'Hours Worked', heuresSup:'Overtime', joursAbsence:'Absence Days'}}
              uploadEndpoint="/api/timesheets/upload-csv"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('timesheets')}
            />
          )}

          {view==='absences' && (
            <Section
              title="Absences"
              data={filterData(cache.absences || [])}
              columns={['id', 'type', 'dateDebut', 'dateFin', 'justifie']}
              columnLabels={{id:'ID', type:'Type', dateDebut:'Start Date', dateFin:'End Date', justifie:'Justified'}}
              uploadEndpoint="/api/absences/upload-csv"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('absences')}
            />
          )}

          {view==='contracts' && (
            <Section
              title="Contracts"
              data={filterData(cache.contracts || [])}
              columns={['id', 'typeContrat', 'tempsTravail', 'dateDebut', 'dateFin', 'coefficient']}
              columnLabels={{id:'ID', typeContrat:'Contract Type', tempsTravail:'Work Hours', dateDebut:'Start Date', dateFin:'End Date', coefficient:'Coefficient'}}
              uploadEndpoint="/api/contracts/upload-csv"
              onUpload={handleUpload}
              onSort={sortBy}
              uploading={uploading}
              downloadSample={()=>downloadSample('contracts')}
            />
          )}
        </main>
      </div>
    </div>
  )
}

function Section({title, data, columns, columnLabels, uploadEndpoint, onUpload, onSort, uploading, downloadSample}){
  return (
    <section className="data-section">
      <div className="section-toolbar">
        <div className="toolbar-left">
          <span className="record-count">📊 {data.length} records</span>
        </div>
        <div className="toolbar-right">
          <label className="file-input-label">
            📤 Upload CSV
            <input type="file" accept=".csv" onChange={(e)=>onUpload(e, uploadEndpoint)} disabled={uploading} />
          </label>
          <button onClick={downloadSample} className="btn-secondary">📥 Sample CSV</button>
        </div>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <thead>
            <tr>
              {columns.map(col=> (
                <th key={col} onClick={()=>onSort(col)} className="sortable">
                  {columnLabels[col] || col} <span className="sort-hint">↕</span>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.length === 0 ? (
              <tr><td colSpan={columns.length} className="empty-row">No data available</td></tr>
            ) : (
              data.map((row, idx)=> (
                <tr key={idx} className="table-row">
                  {columns.map(col=> (
                    <td key={col}>{formatValue(row[col])}</td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  )
}

function formatValue(val){
  if(val === null || val === undefined) return '—'
  if(typeof val === 'boolean') return val ? '✓ Yes' : '✗ No'
  if(typeof val === 'number') return val.toLocaleString()
  return String(val)
}
