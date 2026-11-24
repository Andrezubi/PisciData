FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

COPY PisciDataBackend/PisciDataBackend.csproj PisciDataBackend/
COPY PisciDataMCP/PisciDataMCP.csproj PisciDataMCP/
RUN dotnet restore PisciDataBackend/PisciDataBackend.csproj \
    && dotnet restore PisciDataMCP/PisciDataMCP.csproj

COPY PisciDataBackend/ PisciDataBackend/
COPY PisciDataMCP/ PisciDataMCP/

RUN dotnet publish PisciDataBackend/PisciDataBackend.csproj -c Release -o /app/publish /p:UseAppHost=false \
    && dotnet build PisciDataMCP/PisciDataMCP.csproj -c Release -o /app/mcp /p:SelfContained=false /p:PublishSelfContained=false /p:PublishSingleFile=false /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .
COPY --from=build /app/mcp ./mcp
COPY PisciDataBackend/SqlInit/ ./SqlInit/

ENV ASPNETCORE_ENVIRONMENT=Production
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "export PORT=${PORT:-8080}; export ASPNETCORE_URLS=http://0.0.0.0:${PORT}; export AI__MCP__ServerDll=/app/mcp/PisciDataMCP.dll; export PisciDataApi__BaseUrl=http://127.0.0.1:${PORT}/api; exec dotnet PisciDataBackend.dll"]
