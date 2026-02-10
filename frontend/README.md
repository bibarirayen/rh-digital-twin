# RH Frontend

Minimal Vite + React frontend for RH Digital Twin.

Run:

```bash
cd frontend
npm install
# set backend base if different (default empty -> same origin)
# export VITE_API_BASE_URL=http://localhost:8081
npm run dev
```

Notes:
- If backend runs on a different origin, either set `VITE_API_BASE_URL` or enable CORS on the backend.
- Upload forms send `file` as form-data to the backend CSV endpoints.
