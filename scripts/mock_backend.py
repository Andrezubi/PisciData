#!/usr/bin/env python3
"""Backend SIMULADO de PisciData (solo lectura) para probar el MCP/Qwen cuando no hay MySQL.
Expone los mismos endpoints GET que usa PisciDataMCP en http://localhost:5007/api."""
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

D = {
    "farms": [
        {"id": 1, "name": "Piscigranja Los Andes", "location": "Cochabamba", "isActive": True},
        {"id": 2, "name": "Granja El Remanso", "location": "Santa Cruz", "isActive": True},
    ],
    "ponds": [
        {"id": 1, "farmId": 1, "name": "Estanque 1", "volumeM3": 120.0, "maxDensityKgM3": 25.0},
        {"id": 2, "farmId": 1, "name": "Estanque 2", "volumeM3": 90.0, "maxDensityKgM3": 22.0},
        {"id": 3, "farmId": 2, "name": "Estanque 3", "volumeM3": 150.0, "maxDensityKgM3": 30.0},
    ],
    "productioncycles": [
        {"id": 1, "pondId": 3, "species": "Tilapia", "startDate": "2026-08-01", "initialFishCount": 5000, "initialAverageWeightGrams": 5.0, "status": "Active"},
        {"id": 2, "pondId": 1, "species": "Pacú", "startDate": "2026-09-10", "initialFishCount": 3000, "initialAverageWeightGrams": 8.0, "status": "Active"},
    ],
    "biometrics": [
        {"id": 1, "productionCycleId": 1, "date": "2026-09-20", "averageWeightGrams": 85.5, "estimatedBiomassKg": 410.0, "recommendedDailyFeedKg": 12.3, "health": "Buena"},
        {"id": 2, "productionCycleId": 1, "date": "2026-10-04", "averageWeightGrams": 142.0, "estimatedBiomassKg": 690.0, "recommendedDailyFeedKg": 17.2, "health": "Buena"},
    ],
    "feedings": [
        {"id": 1, "productionCycleId": 1, "date": "2026-10-10", "feedKg": 16.5, "feedType": "Balanceado 35% proteína"},
        {"id": 2, "productionCycleId": 1, "date": "2026-10-11", "feedKg": 17.0, "feedType": "Balanceado 35% proteína"},
    ],
}

class H(BaseHTTPRequestHandler):
    def _send(self, code, obj):
        b = json.dumps(obj).encode()
        self.send_response(code); self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(b))); self.end_headers(); self.wfile.write(b)

    def do_GET(self):
        parts = [p for p in self.path.split("?")[0].split("/") if p]
        if len(parts) < 2 or parts[0] != "api": return self._send(404, {"error": "not found"})
        rows = D.get(parts[1].lower())
        if rows is None: return self._send(404, {"error": "unknown resource"})
        if len(parts) == 2: return self._send(200, rows)
        item = next((r for r in rows if str(r["id"]) == parts[2]), None)
        return self._send(200, item) if item else self._send(404, {"error": "not found"})

    def log_message(self, *a): pass

if __name__ == "__main__":
    print("Mock backend en http://localhost:5007/api")
    ThreadingHTTPServer(("0.0.0.0", 5007), H).serve_forever()
