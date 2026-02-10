Frontend quickstart

1. Install dependencies:

```powershell
cd frontend
npm install
```

2. Run dev server:

```powershell
npm run dev
```

3. If backend on different port (e.g., 8081), set env before running dev:

```powershell
$env:VITE_API_BASE_URL = 'http://localhost:8081'
npm run dev
```
