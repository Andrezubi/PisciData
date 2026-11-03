#!/usr/bin/env bash
# Levanta el stack completo: Qwen (llama-server) + Backend + Chat/MCP.
# Uso:  scripts/start-all.sh [--mock]      (--mock = backend simulado, sin MySQL)
#       scripts/start-all.sh --stop
set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN="$ROOT/.run"; mkdir -p "$RUN"
MODEL="${QWEN_GGUF:-}"
LLM_PORT=8080; API_PORT=5007; CHAT_PORT=5050

stop_all() {
  for f in "$RUN"/*.pid; do [ -f "$f" ] && kill "$(cat "$f")" 2>/dev/null && echo "detenido $(basename "$f" .pid)"; rm -f "$f"; done
}
[ "${1:-}" = "--stop" ] && { stop_all; exit 0; }
MOCK=0; [ "${1:-}" = "--mock" ] && MOCK=1

port_up() { (exec 3<>/dev/tcp/127.0.0.1/"$1") 2>/dev/null; }
wait_for() { # url nombre timeout
  for _ in $(seq 1 "$3"); do curl -sf -m 2 "$1" >/dev/null 2>&1 && { echo "✔ $2 listo"; return 0; }; sleep 1; done
  echo "✘ $2 no respondió (ver $RUN/*.log)"; return 1
}
start() { # nombre cmd...
  local n="$1"; shift
  nohup "$@" >"$RUN/$n.log" 2>&1 & echo $! >"$RUN/$n.pid"
}

stop_all >/dev/null 2>&1

# 1) LLM local --------------------------------------------------------------
if port_up $LLM_PORT; then echo "• llama-server ya corre en :$LLM_PORT"
else
  [ -n "$MODEL" ] && [ -f "$MODEL" ] || { echo "Define QWEN_GGUF con la ruta local al archivo GGUF"; exit 1; }
  echo "→ Iniciando Qwen (CPU, puede tardar ~30s en cargar)…"
  start llm llama-server -m "$MODEL" --port $LLM_PORT --host 127.0.0.1 -c 16384 -t "$(nproc)" --jinja --reasoning off --alias qwen3.5-4b
fi

# 2) Backend ---------------------------------------------------------------
if port_up $API_PORT; then echo "• Backend ya corre en :$API_PORT"
elif [ $MOCK -eq 1 ]; then
  echo "→ Iniciando backend SIMULADO (sin MySQL)"
  start api python3 "$ROOT/scripts/mock_backend.py"
else
  if ! port_up 3306; then
    echo "⚠ MySQL no responde en :3306. El backend real fallará al consultar datos."
    echo "  Usa  scripts/start-all.sh --mock  para probar con datos simulados."
  fi
  echo "→ Iniciando PisciDataBackend real"
  start api dotnet run --project "$ROOT/PisciDataBackend/PisciDataBackend.csproj" --launch-profile http
fi

# 3) MCP + Chat (compilar MCP primero: el chat lo lanza por stdio) -----------
echo "→ Compilando MCP y Chat…"
dotnet build "$ROOT/PisciDataMCP/PisciDataMCP.csproj" -v q --nologo >"$RUN/build.log" 2>&1 \
  && dotnet build "$ROOT/PisciDataChat/PisciDataChat.csproj" -v q --nologo >>"$RUN/build.log" 2>&1 \
  || { echo "✘ Falló la compilación:"; tail -20 "$RUN/build.log"; exit 1; }
start chat dotnet run --no-build --project "$ROOT/PisciDataChat/PisciDataChat.csproj"

wait_for "http://127.0.0.1:$LLM_PORT/health" "Qwen" 120
wait_for "http://127.0.0.1:$CHAT_PORT/api/health" "Chat+MCP" 40
[ $MOCK -eq 1 ] && wait_for "http://127.0.0.1:$API_PORT/api/farms" "Backend (mock)" 10

echo; echo "🌐 Interfaz:  http://localhost:$CHAT_PORT"
echo "   Detener:   scripts/start-all.sh --stop    Logs: $RUN/*.log"
(command -v xdg-open >/dev/null && xdg-open "http://localhost:$CHAT_PORT" >/dev/null 2>&1) || true
