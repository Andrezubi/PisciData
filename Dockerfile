FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

COPY PisciDataBackend/PisciDataBackend.csproj PisciDataBackend/
RUN dotnet restore PisciDataBackend/PisciDataBackend.csproj

COPY PisciDataBackend/ PisciDataBackend/
WORKDIR /src/PisciDataBackend
RUN dotnet publish PisciDataBackend.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .
COPY PisciDataBackend/SqlInit/ ./SqlInit/

ENV ASPNETCORE_ENVIRONMENT=Production
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "ASPNETCORE_URLS=http://0.0.0.0:${PORT:-8080} exec dotnet PisciDataBackend.dll"]
